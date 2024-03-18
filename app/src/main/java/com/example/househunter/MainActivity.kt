package com.example.househunter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var roomAdapter: RoomAdapter
    private lateinit var roomsList: MutableList<Room>
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        roomsList = mutableListOf()
        roomAdapter = RoomAdapter(roomsList)
        recyclerView.adapter = roomAdapter

        databaseRef = FirebaseDatabase.getInstance().reference.child("rooms")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                roomsList.clear()
                val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
                val usersRef = FirebaseDatabase.getInstance().reference.child("users").child(currentUserUid ?: "")

                usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(userSnapshot: DataSnapshot) {
                        val userData = userSnapshot.getValue(User::class.java)

                        dataSnapshot.children.forEach { roomSnapshot ->
                            val room = roomSnapshot.getValue(Room::class.java)
                            if (room != null && userData != null && userData.locate == room.locate) {
                                roomsList.add(room)
                            }
                        }

                        roomAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle error
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })

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

        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        val usersRef = FirebaseDatabase.getInstance().reference.child("users").child(currentUserUid ?: "")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userData = dataSnapshot.getValue(User::class.java)

                if (userData != null) {
                    val locate = userData.locate

                    locate?.let {
                        val aiTextView = findViewById<TextView>(R.id.AI)
                        aiTextView.text = it
                    }
                } else {
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 에러 처리
            }
        })


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
