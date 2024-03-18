package com.example.househunter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

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

        findViewById<View>(R.id.room1).setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("selectedRoomType", "원룸")
            startActivity(intent)
        }

        val room1 = findViewById<VideoView>(R.id.room1_videoView)
        val room1Url = "https://cdn-icons-mp4.flaticon.com/512/8121/8121334.mp4"

        val room1videoUri = Uri.parse(room1Url)
        room1.setVideoURI(room1videoUri)
        room1.setOnPreparedListener { mp ->
            mp.isLooping = true // 비디오를 반복 재생하도록 설정
        }
        room1.start()

        findViewById<View>(R.id.room2).setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("selectedRoomType", "투ㆍ쓰리룸")
            startActivity(intent)
        }

        val room2 = findViewById<VideoView>(R.id.room2_videoView)
        val room2Url = "https://cdn-icons-mp4.flaticon.com/512/8722/8722487.mp4"

        val room2videoUri = Uri.parse(room2Url)
        room2.setVideoURI(room2videoUri)
        room2.setOnPreparedListener { mp ->
            mp.isLooping = true // 비디오를 반복 재생하도록 설정
        }
        room2.start()

        findViewById<View>(R.id.officetel).setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("selectedRoomType", "오피스텔")
            startActivity(intent)
        }

        val officetel = findViewById<VideoView>(R.id.officetel_videoView)
        val officetelUrl = "https://cdn-icons-mp4.flaticon.com/512/8722/8722412.mp4"

        val officetelvideoUri = Uri.parse(officetelUrl)
        officetel.setVideoURI(officetelvideoUri)
        officetel.setOnPreparedListener { mp ->
            mp.isLooping = true // 비디오를 반복 재생하도록 설정
        }
        officetel.start()

        findViewById<View>(R.id.apartment).setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("selectedRoomType", "아파트")
            startActivity(intent)
        }

        val apartment = findViewById<VideoView>(R.id.apartment_videoView)
        val apartmentUrl = "https://cdn-icons-mp4.flaticon.com/512/8112/8112865.mp4"

        val apartmentvideoUri = Uri.parse(apartmentUrl)
        apartment.setVideoURI(apartmentvideoUri)
        apartment.setOnPreparedListener { mp ->
            mp.isLooping = true // 비디오를 반복 재생하도록 설정
        }
        apartment.start()

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