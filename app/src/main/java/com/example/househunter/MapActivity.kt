package com.example.househunter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

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

    override fun onMapReady(naverMap: NaverMap) {
        // 지도에 첫 번째 마커 표시
        val marker = Marker()
        marker.position = LatLng(37.5670135, 126.9783740)
        marker.map = naverMap

        // 지도에 두 번째 마커 표시
        val marker2 = Marker()
        marker2.position = LatLng(37.563119, 126.969418)
        marker2.map = naverMap
    }

}
