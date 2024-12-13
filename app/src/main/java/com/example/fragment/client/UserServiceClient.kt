package com.example.fragment.client

import com.example.fragment.dto.UserDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UserServiceClient {
    @GET("users/{userId}")
    fun getUser(@Path("userId") userId: Long): Call<UserDto>
}