package com.example.househunter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class LikeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val homeintent = Intent(this, MainActivity::class.java)
                    startActivity(homeintent)
                    true
                }
                R.id.like -> {
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
        bottomNavigationView.selectedItemId = R.id.like
    }
}