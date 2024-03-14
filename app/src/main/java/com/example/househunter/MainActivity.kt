package com.example.househunter

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.database.database


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val database = Firebase.database
        val myRef = database.getReference("message")


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
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
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.home
    }
}