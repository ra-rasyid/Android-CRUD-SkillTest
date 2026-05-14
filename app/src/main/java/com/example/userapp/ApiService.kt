package com.example.userapp

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("users") // Endpoint untuk list user di JSONPlaceholder
    fun getUsers(): Call<List<User>> // Di sini langsung List<User>, bukan UserResponse
}