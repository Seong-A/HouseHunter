package com.example.househunter

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class MainActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private lateinit var roomsQuery: Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        roomsQuery = database.getReference("rooms")

        findViewById<View>(R.id.logo).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.room).setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("selectedRoomType", "원룸")
            startActivity(intent)
        }

        findViewById<View>(R.id.sell).setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("selectedRoomType", "투ㆍ쓰리룸")
            startActivity(intent)
        }

        findViewById<View>(R.id.officetel).setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("selectedRoomType", "오피스텔")
            startActivity(intent)
        }

        findViewById<View>(R.id.apart).setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("selectedRoomType", "아파트")
            startActivity(intent)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
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
                    val mypageintent = Intent(this, MypageActivity::class.java)
                    startActivity(mypageintent)
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.home
    }
}