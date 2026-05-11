package com.hakankuru.eventhub.data.remote

import com.hakankuru.eventhub.data.remote.request.AuthResponse
import com.hakankuru.eventhub.data.remote.request.LoginRequest
import com.hakankuru.eventhub.data.remote.request.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): AuthResponse

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse
}