package com.example.househunter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdateUserInfoActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnSaveChanges: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user_info)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        etName = findViewById(R.id.user_name)
        etEmail = findViewById(R.id.user_email)
        etNewPassword = findViewById(R.id.new_password)
        etPhone = findViewById(R.id.user_phone)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)

        // 로고 클릭 시 MainActivity로 이동
        findViewById<View>(R.id.logo).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnSaveChanges.setOnClickListener {
            saveChanges()
        }

        // 사용자 정보 가져오기 및 표시
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        currentUser?.let { user ->
            etEmail.setText(user.email)
            etEmail.isEnabled = false
            getUserInfo(user.uid)
        }
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        return if (phoneNumber.length == 11) {
            "${phoneNumber.substring(0, 3)}-${phoneNumber.substring(3, 7)}-${phoneNumber.substring(7)}"
        } else {
            phoneNumber // 형식에 맞지 않는 경우 그대로 반환
        }
    }

    private fun getUserInfo(uid: String) {
        val userRef = databaseReference.child("users").child(uid)
        userRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val name = dataSnapshot.child("name").getValue(String::class.java)
                val phoneNumber = dataSnapshot.child("phoneNumber").getValue(String::class.java)
                etName.setText(name)
                etPhone.setText(phoneNumber?.let { formatPhoneNumber(it) })
                etName.isEnabled = false
                etPhone.isEnabled = false
            }
        }.addOnFailureListener {
            Toast.makeText(this@UpdateUserInfoActivity, "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveChanges() {
        val newName = etName.text.toString().trim()
        val newPhone = etPhone.text.toString().trim()
        val newPassword = etNewPassword.text.toString().trim()

        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        currentUser?.let { user ->
            val uid = user.uid
            val userRef = databaseReference.child("users").child(uid)

            // 이름과 전화번호 업데이트
            userRef.child("name").setValue(newName)
            userRef.child("phone").setValue(newPhone)

            // 비밀번호 업데이트
            if (newPassword.isNotEmpty()) {
                user.updatePassword(newPassword).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@UpdateUserInfoActivity, "비밀번호가 성공적으로 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@UpdateUserInfoActivity, "비밀번호 업데이트 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            Toast.makeText(this@UpdateUserInfoActivity, "변경 사항이 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}


