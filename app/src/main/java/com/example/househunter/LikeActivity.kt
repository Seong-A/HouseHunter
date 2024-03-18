package com.example.househunter

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LikeActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val likeroomContainer: LinearLayout = findViewById(R.id.likeroomContainer)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

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
                                            val roomId = roomDataSnapshot.key
                                            val intent = Intent(this@LikeActivity, RoomDetailActivity::class.java).apply {
                                                putExtra("roomId", roomId)
                                                putExtra("locate", room.locate)
                                            }
                                            startActivity(intent)
                                        }

                                        likeroomContainer.addView(roomLayout)

                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // 처리 실패 시 동작
                                }
                            })
                        }
                    } else {
                        // 사용자의 Favorite 데이터가 없는 경우 처리
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 처리 실패 시 동작
                }
            })
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val homeintent = Intent(this, MainActivity::class.java)
                    startActivity(homeintent)
                    finish() // 현재 액티비티를 종료합니다.
                    true
                }
                R.id.like -> {
                    // 이미 LikeActivity에 있으므로 아무것도 하지 않습니다.
                    true
                }
                R.id.map -> {
                    val mapintent = Intent(this, MapActivity::class.java)
                    startActivity(mapintent)
                    finish() // 현재 액티비티를 종료합니다.
                    true
                }
                R.id.mypage -> {
                    val mypageintent = Intent(this, MypageActivity::class.java)
                    startActivity(mypageintent)
                    finish() // 현재 액티비티를 종료합니다.
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.like
    }
}
