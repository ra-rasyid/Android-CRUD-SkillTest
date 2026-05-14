package com.example.userapp

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("data") val data: List<User>
)

data class User(
    val id: Int,
    val name: String,
    val email: String? = null,
    val avatar: String? = "https://i.pravatar.cc/150?u="
)