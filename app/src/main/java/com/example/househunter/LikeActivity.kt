package com.example.househunter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.naver.maps.map.NaverMapSdk

class LikeActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var likeroomContainer: LinearLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private val MAX_RECENT_ITEMS = 10 // 표시할 최대 최근 항목 수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        val likeListMenuItem = findViewById<BottomNavigationView>(R.id.like_list)
        likeroomContainer = findViewById(R.id.likeroomContainer)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        likeListMenuItem.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.recently -> {
                    updateRecentlyViewedRooms()
                    true
                }
                R.id.like -> {
                    updateFavoriteRooms()
                    true
                }
                else -> false
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val homeIntent = Intent(this, MainActivity::class.java)
                    startActivity(homeIntent)
                    finish()
                    true
                }
                R.id.like -> {
                    true
                }
                R.id.map -> {
                    val mapIntent = Intent(this, MapActivity::class.java)
                    startActivity(mapIntent)
                    finish()
                    true
                }
                R.id.mypage -> {
                    val mypageIntent = Intent(this, MypageActivity::class.java)
                    startActivity(mypageIntent)
                    finish()
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.like

        // 네이버 지도 클라이언트 ID 가져오기
        val naverClientId = getString(R.string.NAVER_CLIENT_ID)

        // 네이버 지도 SDK에 클라이언트 ID 설정
        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient(naverClientId)

        updateRecentlyViewedRooms()
    }

    override fun onResume() {
        super.onResume()

        // 현재 선택된 메뉴에 따라 다른 함수 호출
        val selectedMenuId = bottomNavigationView.selectedItemId
        when (selectedMenuId) {
            R.id.recently -> updateRecentlyViewedRooms()
            R.id.like -> updateFavoriteRooms()
        }
    }

    private fun updateRecentlyViewedRooms() {
        likeroomContainer.removeAllViews()
        val currentUser = auth.currentUser
        currentUser?.let {
            val userId = it.uid
            val userRef = database.child("users").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild("Recently")) {
                        val recentRooms = dataSnapshot.child("Recently").children
                            .mapNotNull { roomSnapshot ->
                                val roomId = roomSnapshot.key
                                val timestampString = roomSnapshot.child("timestamp").value as String
                                val timestamp = timestampString.toLong()
                                Pair(roomId, timestamp)
                            }
                            .sortedByDescending { it.second } // timestamp를 기준으로 내림차순으로 정렬
                            .take(MAX_RECENT_ITEMS)

                        for ((roomId, _) in recentRooms) {
                            val roomRef = database.child("rooms").child(roomId ?: "")
                            roomRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(roomDataSnapshot: DataSnapshot) {
                                    val room = roomDataSnapshot.getValue(Room::class.java)
                                    room?.let {
                                        val roomLayout = layoutInflater.inflate(R.layout.likeroom_layout, null)
                                        val moneyTextView: TextView = roomLayout.findViewById(R.id.money)
                                        val rtypeTextView: TextView = roomLayout.findViewById(R.id.rtype)
                                        val roomInfoTextView: TextView = roomLayout.findViewById(R.id.like_room_info)

                                        // 방 정보 설정
                                        moneyTextView.text = "월세 ${room.fix_money} / ${room.monthly_money}"
                                        rtypeTextView.text = room.rtype
                                        roomInfoTextView.text = "${room.floor}, 관리비 ${room.management_money}만"

                                        // ViewPager 설정
                                        val viewPager: ViewPager = roomLayout.findViewById(R.id.likeroom_imageArea)

                                        // 사진이 있는 경우와 없는 경우에 따라 어댑터 설정
                                        val adapter = if (room.photos != null && room.photos.isNotEmpty()) {
                                            PhotoPagerAdapter(room.photos.values.toList())
                                        } else {
                                            PhotoPagerAdapter(emptyList())
                                        }
                                        viewPager.adapter = adapter

                                        roomLayout.setOnClickListener {
                                            val roomID = roomDataSnapshot.key
                                            val intent = Intent(this@LikeActivity, RoomDetailActivity::class.java).apply {
                                                putExtra("roomID", roomID)
                                                Log.d("LikeActivity", "Room ID: $roomID")
                                                putExtra("locate", room.locate)
                                            }
                                            val currentUser = auth.currentUser
                                            currentUser?.let {
                                                val userId = it.uid
                                                val userRef = database.child("users").child(userId).child("Recently")
                                                val timestamp = System.currentTimeMillis().toString()
                                                val updateMap = mapOf(roomID to mapOf("roomID" to roomID, "timestamp" to timestamp))
                                                userRef.updateChildren(updateMap)
                                            }
                                            startActivity(intent)
                                        }

                                        likeroomContainer.addView(roomLayout)
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // onCancelled 이벤트 처리
                                }
                            })
                        }
                    } else {
                        // "Recently" 노드가 없거나 비어있는 경우 처리
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // onCancelled 이벤트 처리
                }
            })
        }
    }

    private fun updateFavoriteRooms() {
        likeroomContainer.removeAllViews()
        val currentUser = auth.currentUser
        currentUser?.let {
            val userId = it.uid
            val userRef = database.child("users").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild("Favorite")) {
                        val favoriteRooms = dataSnapshot.child("Favorite").children
                        for (roomSnapshot in favoriteRooms) {
                            val roomId = roomSnapshot.key
                            val roomRef = database.child("rooms").child(roomId ?: "")
                            roomRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(roomDataSnapshot: DataSnapshot) {
                                    val room = roomDataSnapshot.getValue(Room::class.java)
                                    room?.let {
                                        val roomLayout = layoutInflater.inflate(R.layout.likeroom_layout, null)
                                        val moneyTextView: TextView = roomLayout.findViewById(R.id.money)
                                        val rtypeTextView: TextView = roomLayout.findViewById(R.id.rtype)
                                        val roomInfoTextView: TextView = roomLayout.findViewById(R.id.like_room_info)

                                        // 데이터 설정
                                        moneyTextView.text = "월세 ${room.fix_money} / ${room.monthly_money}"
                                        rtypeTextView.text = room.rtype
                                        roomInfoTextView.text = "${room.floor}, 관리비 ${room.management_money}만"

                                        // 각 roomLayout마다 새로운 ViewPager 생성
                                        val viewPager: ViewPager = roomLayout.findViewById(R.id.likeroom_imageArea)

                                        // 사진이 있는 경우에만 어댑터 생성 및 설정
                                        if (room.photos != null && room.photos.isNotEmpty()) {
                                            val adapter = PhotoPagerAdapter(room.photos.values.toList())
                                            viewPager.adapter = adapter
                                        } else {
                                            // 사진이 없는 경우, 빈 리스트를 이용하여 어댑터 생성
                                            val adapter = PhotoPagerAdapter(emptyList())
                                            viewPager.adapter = adapter
                                        }

                                        roomLayout.setOnClickListener {
                                            val roomID = roomDataSnapshot.key
                                            val intent = Intent(this@LikeActivity, RoomDetailActivity::class.java).apply {
                                                putExtra("roomID", roomID)
                                                Log.d("LikeActivity", "Room ID: $roomID")
                                                putExtra("locate", room.locate)
                                            }
                                            val currentUser = auth.currentUser
                                            currentUser?.let {
                                                val userId = it.uid
                                                val userRef = database.child("users").child(userId).child("Recently")
                                                val timestamp = System.currentTimeMillis().toString()
                                                val updateMap = mapOf(roomID to mapOf("roomID" to roomID, "timestamp" to timestamp))
                                                userRef.updateChildren(updateMap)
                                            }
                                            startActivity(intent)
                                        }

                                        likeroomContainer.addView(roomLayout)
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // onCancelled 이벤트 처리
                                }
                            })
                        }
                    } else {
                        // "Favorite" 노드가 없는 경우 처리
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // onCancelled 이벤트 처리
                }
            })
        }
    }
}
