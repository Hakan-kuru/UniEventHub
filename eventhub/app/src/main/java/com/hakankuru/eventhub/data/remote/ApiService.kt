package com.hakankuru.eventhub.data.remote

import com.hakankuru.eventhub.data.remote.request.AuthResponse
import com.hakankuru.eventhub.data.remote.request.LoginRequest
import com.hakankuru.eventhub.data.remote.request.RegisterRequest
import com.hakankuru.eventhub.data.remote.request.ClubCreateRequest
import com.hakankuru.eventhub.data.remote.request.EventCreateRequest
import com.hakankuru.eventhub.data.remote.response.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

interface ApiService {

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): AuthResponse

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse

    @GET("v1/me")
    suspend fun getCurrentUser(): Response<UserProfileResponse>

    @POST("v1/clubs")
    suspend fun createClub(@Body request: ClubCreateRequest): Response<Any>

    @POST("v1/clubs/{clubId}/assign-admin")
    suspend fun assignClubAdmin(
        @retrofit2.http.Path("clubId") clubId: Long, 
        @retrofit2.http.Query("userEmail") userEmail: String
    ): Response<Unit>

    @POST("v1/clubs/{clubId}/members")
    suspend fun addMember(
        @retrofit2.http.Path("clubId") clubId: Long, 
        @retrofit2.http.Query("userEmail") userEmail: String,
        @retrofit2.http.Query("role") role: String = "MEMBER"
    ): Response<Unit>

    @retrofit2.http.DELETE("v1/clubs/{clubId}/members/{userId}")
    suspend fun removeMember(
        @retrofit2.http.Path("clubId") clubId: Long, 
        @retrofit2.http.Path("userId") userId: Long
    ): Response<Unit>

    @POST("v1/events")
    suspend fun createEvent(@Body request: EventCreateRequest): Response<Any>
}