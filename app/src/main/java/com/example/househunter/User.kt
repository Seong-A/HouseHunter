package com.example.househunter

data class User(
    val uid : String? = null,
    val email: String? = null,
    val name: String? = null,
    val locate: String? = null,
    val phoneNumber: String? = null,
    val favorite: Map<String, Boolean>? = null
)