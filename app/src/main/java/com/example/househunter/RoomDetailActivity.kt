package com.example.househunter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roomdetail)

        findViewById<View>(R.id.logo).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        roomID = intent.getStringExtra("roomID")
        Log.d("RoomDetailActivity", "Room ID: $roomID")

        databaseReference = FirebaseDatabase.getInstance().reference.child("rooms")

        roomID?.let { roomId ->
            databaseReference.child(roomId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val roomData = dataSnapshot.getValue(Room::class.java)

                    roomData?.let {
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

                        val managementmTextView = findViewById<TextView>(R.id.management_m)
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

//                        val viewPager: ViewPager = findViewById(R.id.viewPager)
//                        val photoUrls: List<String> = roomData.photos

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
        }
    }
}
