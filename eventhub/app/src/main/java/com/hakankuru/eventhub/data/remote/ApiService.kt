package com.hakankuru.eventhub.data.remote

import com.hakankuru.eventhub.data.remote.request.AuthResponse
import com.hakankuru.eventhub.data.remote.request.LoginRequest
import com.hakankuru.eventhub.data.remote.request.RegisterRequest
import com.hakankuru.eventhub.data.remote.request.ClubCreateRequest
import com.hakankuru.eventhub.data.remote.request.EventCreateRequest
import com.hakankuru.eventhub.data.remote.request.UserUpdateRequest
import com.hakankuru.eventhub.data.remote.request.AssignAdminRequest
import com.hakankuru.eventhub.data.remote.response.AdminResponse
import com.hakankuru.eventhub.data.remote.response.AdminUserDto
import com.hakankuru.eventhub.data.remote.response.ClubMemberResponse
import com.hakankuru.eventhub.data.remote.response.ClubResponse
import com.hakankuru.eventhub.data.remote.response.EventResponse
import com.hakankuru.eventhub.data.remote.response.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.http.Path

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

    @PUT("v1/me")
    suspend fun updateCurrentUser(@Body request: UserUpdateRequest): Response<UserProfileResponse>

    @GET("v1/me/events")
    suspend fun getCurrentUserEvents(): Response<List<EventResponse>>

    @GET("v1/clubs")
    suspend fun getAllClubs(): Response<List<ClubResponse>>

    @POST("v1/clubs")
    suspend fun createClub(@Body request: ClubCreateRequest): Response<ClubResponse>

    @PUT("v1/clubs/{clubId}")
    suspend fun updateClub(
        @retrofit2.http.Path("clubId") clubId: Long,
        @Body request: ClubCreateRequest
    ): Response<ClubResponse>

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

    @GET("v1/clubs/{clubId}/members")
    suspend fun getClubMembers(@retrofit2.http.Path("clubId") clubId: Long): Response<List<ClubMemberResponse>>

    @GET("v1/clubs/{clubId}/events")
    suspend fun getClubEvents(@retrofit2.http.Path("clubId") clubId: Long): Response<List<EventResponse>>

    @POST("v1/events")
    suspend fun createEvent(@Body request: EventCreateRequest): Response<EventResponse>

    @GET("v1/events")
    suspend fun getAllEvents(): Response<List<EventResponse>>

    @POST("v1/events/{eventId}/join")
    suspend fun joinEvent(@retrofit2.http.Path("eventId") eventId: Long): Response<Unit>

    @retrofit2.http.DELETE("v1/events/{eventId}/leave")
    suspend fun leaveEvent(@retrofit2.http.Path("eventId") eventId: Long): Response<Unit>

    // ── SuperAdmin Endpoints ─────────────────────────────────────────────────

    /** POST /api/v1/admin/assign — email ile ADMIN atar, universityId set eder */
    @POST("v1/admin/assign")
    suspend fun assignAdmin(@Body request: AssignAdminRequest): Response<AdminResponse>

    /** DELETE /api/v1/admin/{userId} — Admin rolünü kaldırır */
    @DELETE("v1/admin/{userId}")
    suspend fun removeAdmin(@Path("userId") userId: Long): Response<Unit>

    /** GET /api/v1/admin/list — Tüm adminleri getirir */
    @GET("v1/admin/list")
    suspend fun getAllAdmins(): Response<List<AdminResponse>>

    /** GET /api/v1/admin/users — Sistemdeki tüm kullanıcıları listeler */
    @GET("v1/admin/users")
    suspend fun getAllUsers(): Response<List<AdminUserDto>>
}