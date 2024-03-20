package com.example.househunter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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

    private val database = FirebaseDatabase.getInstance()
    private lateinit var roomsQuery: Query

    private lateinit var recyclerView: RecyclerView
    private lateinit var roomAdapter: RoomAdapter
    private lateinit var roomsList: MutableList<Room>
    private lateinit var databaseRef: DatabaseReference
    private var currentUserUid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        roomsQuery = database.getReference("rooms")
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        roomsList = mutableListOf()
        roomAdapter = RoomAdapter(roomsList)
        recyclerView.adapter = roomAdapter

        databaseRef = database.reference.child("rooms")
        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        setupRoomList()
        setupRoomClickListeners()

        val room1 = findViewById<VideoView>(R.id.room1_videoView)
        val room1Url = "https://cdn-icons-mp4.flaticon.com/512/8121/8121334.mp4"

        val room1videoUri = Uri.parse(room1Url)
        room1.setVideoURI(room1videoUri)
        room1.setOnPreparedListener { mp ->
            mp.isLooping = true // 비디오를 반복 재생하도록 설정
        }
        room1.start()

        findViewById<View>(R.id.room2).setOnClickListener {
            startMapActivityWithRoomType("투ㆍ쓰리룸")
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
            startMapActivityWithRoomType("오피스텔")
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
            startMapActivityWithRoomType("아파트")
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
                Log.e("MainActivity", "Firebase database error: ${databaseError.message}")
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

    private fun setupRoomList() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                roomsList.clear()
                val usersRef = database.reference.child("users").child(currentUserUid)

                usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(userSnapshot: DataSnapshot) {
                        val userData = userSnapshot.getValue(User::class.java)

                        dataSnapshot.children.forEach { roomSnapshot ->
                            val room = roomSnapshot.getValue(Room::class.java)
                            if (room != null && userData != null && userData.locate == room.locate) {
                                val modifiedRoom = room.copy(roomID = roomSnapshot.key)
                                roomsList.add(modifiedRoom)
                            }
                        }

                        roomAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("MainActivity", "Firebase database error: ${databaseError.message}")
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("MainActivity", "Firebase database error: ${databaseError.message}")
            }
        })
    }

    private fun setupRoomClickListeners() {
        roomAdapter.setOnItemClickListener { room ->
            addToRecentlyViewedRooms(room)

            val intent = Intent(this@MainActivity, RoomDetailActivity::class.java).apply {
                putExtra("roomID", room.roomID)
                Log.d("MainActivity", "Room ID: ${room.roomID}")
                putExtra("locate", room.locate)
            }
            startActivity(intent)
        }

        findViewById<View>(R.id.room1).setOnClickListener {
            startMapActivityWithRoomType("원룸")
        }

        findViewById<View>(R.id.room2).setOnClickListener {
            startMapActivityWithRoomType("투ㆍ쓰리룸")
        }

        findViewById<View>(R.id.officetel).setOnClickListener {
            startMapActivityWithRoomType("오피스텔")
        }

        findViewById<View>(R.id.apartment).setOnClickListener {
            startMapActivityWithRoomType("아파트")
        }
    }

    private fun addToRecentlyViewedRooms(room: Room) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        currentUserUid?.let { uid ->
            val userRef = FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Recently")
            val timestamp = System.currentTimeMillis().toString()
            val roomData = mapOf(
                room.roomID to mapOf(
                    "roomID" to room.roomID,
                    "timestamp" to timestamp
                )
            )
            userRef.updateChildren(roomData)
        }
    }

    private fun startMapActivityWithRoomType(roomType: String) {
        val intent = Intent(this, MapActivity::class.java).apply {
            putExtra("selectedRoomType", roomType)
        }
        startActivity(intent)
    }
}