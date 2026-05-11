package com.hakankuru.eventhub.data.remote

import com.hakankuru.eventhub.data.model.LoginRequest
import com.hakankuru.eventhub.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<String>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<String>
}