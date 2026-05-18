package com.example.userapp

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("data") val data: List<User>
)

data class User(
    val id: Int,
    val name: String,
    val email: String? = null,

    // Ganti 'address' menjadi 'userAddress' agar tidak bentrok dengan
    // objek 'address' yang dikirim oleh API JSONPlaceholder
    @SerializedName("lokasi_manual") // Ini opsional, agar tidak bingung
    val address: String? = null,

    val avatar: String? = "https://i.pravatar.cc/150?u="
)