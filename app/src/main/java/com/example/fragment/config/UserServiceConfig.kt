package com.example.fragment.config

import com.example.fragment.client.UserServiceClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserServiceConfig {
    private const val BASE_URL = "http://10.0.2.2:8080"

    val instance: UserServiceClient by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(UserServiceClient::class.java)
    }
}