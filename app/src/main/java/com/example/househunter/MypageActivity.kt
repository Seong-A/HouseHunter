package com.example.househunter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MypageActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        findViewById<View>(R.id.logo).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val userTextView = findViewById<TextView>(R.id.user_name)
        updateUserDisplayName(userTextView)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val homeintent = Intent(this, MainActivity::class.java)
                    startActivity(homeintent)
                    true
                }
                R.id.like -> {
                    val likeintent = Intent(this, LikeActivity::class.java)
                    startActivity(likeintent)
                    true
                }
                R.id.map -> {
                    val mapintent = Intent(this, MapActivity::class.java)
                    startActivity(mapintent)
                    true
                }
                R.id.mypage -> {
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.mypage
    }

    private fun updateUserDisplayName(userTextView: TextView) {
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        currentUser?.let { user ->
            val userId: String = user.uid
            val userRef = databaseReference.child("users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val name = dataSnapshot.child("name").getValue(String::class.java)
                    name?.let {
                        val welcomeMessage = "$it 님의 마이페이지"
                        userTextView.text = welcomeMessage
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@MypageActivity, "사용자 데이터 찾기 실패 ㅠㅠ", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}