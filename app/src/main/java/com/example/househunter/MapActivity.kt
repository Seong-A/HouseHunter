package com.example.househunter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private val database = FirebaseDatabase.getInstance()
    private val roomsRef = database.getReference("rooms")

    private lateinit var naverMap: NaverMap

    private val selectedRoomTypes = mutableSetOf<String>()
    private var selectedFixMoney = 0
    private var selectedMonthlyMoney = 0
    private var selectedManagementMoney = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        findViewById<View>(R.id.logo).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val roomTypes = arrayOf("원룸", "투ㆍ쓰리룸", "오피스텔", "아파트")

        val roomTypeContainer = findViewById<LinearLayout>(R.id.room_type_container)
        val FixmoneyContainer = findViewById<LinearLayout>(R.id.fix_money_container)
        val MonthlymoneyContainer = findViewById<LinearLayout>(R.id.monthly_money_container)
        val ManagementmoneyContainer = findViewById<LinearLayout>(R.id.management_money_container)

        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        val btnApply = findViewById<Button>(R.id.btn_apply)

        // 초기에는 리스트와 버튼을 숨김
        roomTypeContainer.visibility = View.GONE
        FixmoneyContainer.visibility = View.GONE
        MonthlymoneyContainer.visibility = View.GONE
        ManagementmoneyContainer.visibility = View.GONE
        btnCancel.visibility = View.GONE
        btnApply.visibility = View.GONE

        val tvRoomType = findViewById<TextView>(R.id.room_type)
        val tvFixMoney = findViewById<TextView>(R.id.fix_money)
        val tvMonthlyMoney = findViewById<TextView>(R.id.monthly_money)
        val tvManagementMoney = findViewById<TextView>(R.id.management_money)

        tvRoomType.setOnClickListener {
            if (roomTypeContainer.visibility == View.VISIBLE) {
                roomTypeContainer.visibility = View.GONE
                btnCancel.visibility = View.GONE
                btnApply.visibility = View.GONE
            } else {
                roomTypeContainer.visibility = View.VISIBLE
                btnCancel.visibility = View.VISIBLE
                btnApply.visibility = View.VISIBLE
            }
        }

        tvFixMoney.setOnClickListener {
            if (FixmoneyContainer.visibility == View.VISIBLE) {
                FixmoneyContainer.visibility = View.GONE
                btnCancel.visibility = View.GONE
                btnApply.visibility = View.GONE
            } else {
                FixmoneyContainer.visibility = View.VISIBLE
                btnCancel.visibility = View.VISIBLE
                btnApply.visibility = View.VISIBLE
            }
        }

        tvMonthlyMoney.setOnClickListener {
            if (MonthlymoneyContainer.visibility == View.VISIBLE) {
                MonthlymoneyContainer.visibility = View.GONE
                btnCancel.visibility = View.GONE
                btnApply.visibility = View.GONE
            } else {
                MonthlymoneyContainer.visibility = View.VISIBLE
                btnCancel.visibility = View.VISIBLE
                btnApply.visibility = View.VISIBLE
            }
        }

        tvManagementMoney.setOnClickListener {
            if (ManagementmoneyContainer.visibility == View.VISIBLE) {
                ManagementmoneyContainer.visibility = View.GONE
                btnCancel.visibility = View.GONE
                btnApply.visibility = View.GONE
            } else {
                ManagementmoneyContainer.visibility = View.VISIBLE
                btnCancel.visibility = View.VISIBLE
                btnApply.visibility = View.VISIBLE
            }
        }

        for (roomType in roomTypes) {
            val textView = TextView(this)
            textView.text = roomType
            textView.setPadding(30, 8, 30, 8)
            textView.setBackgroundResource(R.drawable.btn_gray_rounded) // 초기 배경
            textView.setOnClickListener {
                // 선택된 방 종류를 집합에 추가 또는 제거
                if (selectedRoomTypes.contains(roomType)) {
                    selectedRoomTypes.remove(roomType)
                    textView.setBackgroundResource(R.drawable.btn_gray_rounded) // 선택 해제
                } else {
                    selectedRoomTypes.add(roomType)
                    textView.setBackgroundResource(R.drawable.btn_red_rounded) // 선택
                }
            }
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 0, 20, 0)
            textView.layoutParams = layoutParams
            roomTypeContainer.addView(textView)
        }

        // 보증금 SeekBar 초기화
        val seekBar1 = findViewById<SeekBar>(R.id.seek_bar1)
        seekBar1.max = 100 // 최대값을 1억으로 설정
        seekBar1.progress = 0 // 초기값을 0만원으로 설정
        seekBar1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedFixMoney = progress * 100 // 1 단위 = 100만원
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        val seekBar2 = findViewById<SeekBar>(R.id.seek_bar2)
        seekBar2.max = 10 // 최대값을 100만원으로 설정
        seekBar2.progress = 0 // 초기값을 0만원으로 설정
        seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedMonthlyMoney = progress * 10 // 1 단위 = 10만원
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        val seekBar3 = findViewById<SeekBar>(R.id.seek_bar3)
        seekBar3.max = 30 // 최대값을 30만원으로 설정
        seekBar3.progress = 0 // 초기값을 0만원으로 설정
        seekBar3.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedManagementMoney = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        btnCancel.setOnClickListener {
            // 취소 버튼 클릭 시 동작 정의
            roomTypeContainer.visibility = View.GONE
            FixmoneyContainer.visibility = View.GONE
            MonthlymoneyContainer.visibility = View.GONE
            ManagementmoneyContainer.visibility = View.GONE
            btnCancel.visibility = View.GONE
            btnApply.visibility = View.GONE
        }

        btnApply.setOnClickListener {
            val selectedRoomTypeText = selectedRoomTypes.joinToString(", ")
            tvRoomType.text = if (selectedRoomTypes.isEmpty()) "방 종류" else selectedRoomTypeText

            // 보증금 값이 0보다 크면 fixmoney에 추가
            if (selectedFixMoney > 0) {
                val formattedDeposit = String.format("%d", selectedFixMoney)
                val moneyTypeText = "$formattedDeposit 만원"
                tvFixMoney.text = moneyTypeText
            }

            // 월세 값이 0보다 크면 monthly_money에 추가
            if (selectedMonthlyMoney > 0) {
                val formattedRent = String.format("%d", selectedMonthlyMoney)
                val moneyTypeText = "$formattedRent 만원"
                tvMonthlyMoney.text = moneyTypeText
            }

            // 관리비가 0보다 크면 management_money에 추가
            if (selectedManagementMoney > 0) {
                val formattedManage = String.format("%d", selectedManagementMoney)
                val moneyTypeText = "$formattedManage 만원"
                tvManagementMoney.text = moneyTypeText
            }

            roomTypeContainer.visibility = View.GONE
            FixmoneyContainer.visibility = View.GONE
            MonthlymoneyContainer.visibility = View.GONE
            ManagementmoneyContainer.visibility = View.GONE
            btnCancel.visibility = View.GONE
            btnApply.visibility = View.GONE
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val homeintent = Intent(this, MainActivity::class.java)
                    startActivity(homeintent)
                    true
                }
                R.id.like -> {
                    val likeintent = Intent(this, LikeActivity::class.java)
                    startActivity(likeintent)
                    true
                }
                R.id.map -> {
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
        bottomNavigationView.selectedItemId = R.id.map

        // 네이버 지도 클라이언트 ID 가져오기
        val naverClientId = getString(R.string.NAVER_CLIENT_ID)

        // 네이버 지도 SDK에 클라이언트 ID 설정
        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient(naverClientId)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction()
                    .add(R.id.map_fragment, it)
                    .commit()
            }
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map

        roomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (roomSnapshot in dataSnapshot.children) {
                    val room = roomSnapshot.getValue(Room::class.java)
                    room?.let { addRoomMarker(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun addRoomMarker(room: Room) {
        val isRoomDisplayed = isRoomMatchFilter(room)
        if (isRoomDisplayed) {
            val marker = Marker()
            marker.position = LatLng(room.latitude ?: 0.0, room.longitude ?: 0.0)
            marker.map = naverMap
        }
    }

    private fun isRoomMatchFilter(room: Room): Boolean {
        val isRoomTypeMatch = selectedRoomTypes.isEmpty() || selectedRoomTypes.contains(room.rtype)
        val isFixMoneyMatch = selectedFixMoney <= 0 || room.fix_money ?: 0 <= selectedFixMoney
        val isMonthlyMoneyMatch = selectedMonthlyMoney <= 0 || room.monthly_money ?: 0 <= selectedMonthlyMoney
        val isManagementMoneyMatch = selectedManagementMoney <= 0 || room.management_money ?: 0 <= selectedManagementMoney
        return isRoomTypeMatch && isFixMoneyMatch && isMonthlyMoneyMatch && isManagementMoneyMatch
    }

}

