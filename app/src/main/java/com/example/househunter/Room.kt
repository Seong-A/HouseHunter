package com.example.househunter

data class Room(
    val roomID: String? = null,
    val id : Long? = null,
    val date: String? = null,
    val elevator: String? = null,
    val fix_money: Long? = 0,
    val floor: String? = null,
    val latitude: Double? = 0.0,
    val location: String? = null,
    val longitude: Double? = 0.0,
    val management_money: Long? = 0,
    val monthly_money: Long? = 0,
    val mtype: String? = null,
    val parking: String? = null,
    val phone: String? = null,
    val photos: HashMap<String, String>? = null,
    val room_bath: String? = null,
    val rtype: String? = null,
    val size: String? = null,
    val total_parking: String? = null,
    val total_room: String? = null
)
