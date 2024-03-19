package com.example.househunter


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val etEmail = findViewById<EditText>(R.id.EditEmail)
        val etPassword = findViewById<EditText>(R.id.EditPass)
        val etConfirmPassword = findViewById<EditText>(R.id.EditInfo)
        val etName = findViewById<EditText>(R.id.EditName)
        val etPhoneNumber = findViewById<EditText>(R.id.EditPhone)

        val btnSignUp = findViewById<Button>(R.id.SignupBtn)

        // 회원가입 버튼 클릭 설정
        btnSignUp.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val name = etName.text.toString().trim()
            val phoneNumber = etPhoneNumber.text.toString().trim()

            // EditText가 비어 있는지 확인
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(name) || TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(this@SignUpActivity, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 비밀번호와 비밀번호 확인이 일치하는지 확인
            if (password != confirmPassword) {
                // 비밀번호 불일치 처리
                etConfirmPassword.error = "비밀번호를 다시 입력하시오"
                return@setOnClickListener
            }

            // Firebase에 사용자 등록
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 회원가입 성공
                        val user = auth.currentUser
                        // Firebase 실시간 데이터베이스에 회원 정보 저장
                        saveUserInfoToDatabase(user?.uid, email, name, phoneNumber)
                        Toast.makeText(this@SignUpActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        // 회원가입 실패
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            // 이미 존재하는 이메일로 가입 시도
                            Toast.makeText(this@SignUpActivity, "해당 이메일은 이미 가입되어 있습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            // 기타 실패 사유
                            Toast.makeText(this@SignUpActivity, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }
    private fun saveUserInfoToDatabase(userId: String?, email: String, name: String, phoneNumber: String) {
        val databaseReference = database.reference.child("users").child(userId ?: "")
        val userInfo = HashMap<String, Any>()
        userInfo["email"] = email
        userInfo["name"] = name
        userInfo["phoneNumber"] = phoneNumber
        databaseReference.setValue(userInfo)
    }
}