package com.example.househunter

data class Room(
    val roomID: String? = null,
    val date: String? = "",
    val elevator: String? = "",
    val fix_money: Long? = 0,
    val floor: String? = "",
    val latitude: Double? = 0.0,
    val location: String? = "",
    val longitude: Double? = 0.0,
    val management_money: Long? = 0,
    val monthly_money: Long? = 0,
    val mtype: String? = "",
    val parking: String? = "",
    val phone: String? = "",
//    val photos: List<String> = listOf(),
    val room_bath: String? = "",
    val rtype: String? = "",
    val size: String? = "",
    val total_parking: String? = "",
    val total_room: String? = ""
) {

}

