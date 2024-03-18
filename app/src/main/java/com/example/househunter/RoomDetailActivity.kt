package com.example.househunter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker

class RoomDetailActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private var roomID: String? = null
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roomdetail)

        findViewById<View>(R.id.logo).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val heartIcon = findViewById<ImageView>(R.id.heartIcon)
        heartIcon.setOnClickListener {
            isFavorite = !isFavorite // 즐겨찾기 상태를 토글

            if (isFavorite) {
                heartIcon.setImageResource(R.drawable.ic_heart_filled)
                addToFavorites(roomID)
            } else {
                heartIcon.setImageResource(R.drawable.ic_heart_empty)
                removeFromFavorites(roomID)
            }
        }

        roomID = intent.getStringExtra("roomID")
        Log.d("RoomDetailActivity", "Room ID: $roomID")

        databaseReference = FirebaseDatabase.getInstance().reference.child("rooms")

        roomID?.let { roomId ->
            databaseReference.child(roomId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val roomData = dataSnapshot.getValue(Room::class.java)

                    roomData?.let {
                        val photoUrls: List<String>? = roomData.photos?.values?.toList()

                        photoUrls?.let { urls ->
                            // ViewPager와 이미지 URL 목록을 사용하여 이미지 표시
                            val viewPager = findViewById<ViewPager>(R.id.viewPager)
                            val adapter = PhotoPagerAdapter(urls)
                            viewPager.adapter = adapter
                        }

                        val textViewTitle = findViewById<TextView>(R.id.textViewTitle)
                        textViewTitle.text = "${roomData.rtype} ${roomData.fix_money} / ${roomData.monthly_money}"

                        val roomTypeTextView = findViewById<TextView>(R.id.roomtype)
                        roomTypeTextView.text = roomData.rtype

                        val sizeTextView = findViewById<TextView>(R.id.size)
                        sizeTextView.text = roomData.size

                        val floorTextView = findViewById<TextView>(R.id.floor)
                        floorTextView.text = roomData.floor

                        val managemoneyTextView = findViewById<TextView>(R.id.management_money)
                        managemoneyTextView.text = roomData.management_money.toString()

                        val monthlymTextView = findViewById<TextView>(R.id.monthly_money)
                        monthlymTextView.text = roomData.monthly_money.toString()

                        val managementmTextView = findViewById<TextView>(R.id.management_money2)
                        managementmTextView.text = roomData.management_money.toString()

                        val parkingTextView = findViewById<TextView>(R.id.parking)
                        parkingTextView.text = roomData.parking

                        val rTypeTextView = findViewById<TextView>(R.id.rtype)
                        rTypeTextView.text = roomData.rtype

                        val floor2TextView = findViewById<TextView>(R.id.floor2)
                        floor2TextView.text = roomData.floor

                        val roombathTextView = findViewById<TextView>(R.id.room_bath)
                        roombathTextView.text = roomData.room_bath

                        val elevatorTextView = findViewById<TextView>(R.id.elevator)
                        elevatorTextView.text = roomData.elevator

                        val totalroomTextView = findViewById<TextView>(R.id.total_room)
                        totalroomTextView.text = roomData.total_room

                        val totalparkingTextView = findViewById<TextView>(R.id.total_parking)
                        totalparkingTextView.text = roomData.total_parking

                        val dateTextView = findViewById<TextView>(R.id.date)
                        dateTextView.text = roomData.date

                        val locationTextView = findViewById<TextView>(R.id.location)
                        locationTextView.text = roomData.location

                        val phoneTextView = findViewById<TextView>(R.id.phone)
                        phoneTextView.text = roomData.phone

                        // 네이버 지도 설정
                        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
                            ?: MapFragment.newInstance().also {
                                supportFragmentManager.beginTransaction().add(R.id.map_fragment, it).commit()
                            }

                        mapFragment.getMapAsync { naverMap ->
                            val latitude = roomData.latitude ?: 0.0
                            val longitude = roomData.longitude ?: 0.0

                            val location = LatLng(latitude, longitude)
                            val marker = Marker()
                            marker.position = location
                            marker.map = naverMap

                            val cameraUpdate = CameraUpdate.scrollTo(location)
                            naverMap.moveCamera(cameraUpdate)
                        }

                        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
                        val isFavoriteRef = FirebaseDatabase.getInstance().reference
                            .child("users")
                            .child(currentUserUid ?: "")
                            .child("Favorite")
                            .child(roomID ?: "")

                        isFavoriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val isFavorite = snapshot.getValue(Boolean::class.java) ?: false
                                if (isFavorite) {
                                    heartIcon.setImageResource(R.drawable.ic_heart_filled)
                                } else {
                                    heartIcon.setImageResource(R.drawable.ic_heart_empty)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle error
                            }
                        })

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    private fun addToFavorites(roomId: String?) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        currentUserUid?.let { uid ->
            val favoriteRef = FirebaseDatabase.getInstance().reference
                .child("users")
                .child(uid)
                .child("Favorite")
                .child(roomId ?: "")
            favoriteRef.setValue(true)
                .addOnSuccessListener {
                    Log.d("RoomDetailActivity", "Room added to favorites")
                }
                .addOnFailureListener { e ->
                    Log.e("RoomDetailActivity", "Error adding room to favorites", e)
                }
        }
    }

    private fun removeFromFavorites(roomId: String?) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        currentUserUid?.let { uid ->
            val favoriteRef = FirebaseDatabase.getInstance().reference
                .child("users")
                .child(uid)
                .child("Favorite")
                .child(roomId ?: "")
            favoriteRef.removeValue()
                .addOnSuccessListener {
                    Log.d("RoomDetailActivity", "Room removed from favorites")
                }
                .addOnFailureListener { e ->
                    Log.e("RoomDetailActivity", "Error removing room from favorites", e)
                }
        }
    }

}