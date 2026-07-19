package com.hakankuru.eventhub.domain.repository

import com.hakankuru.eventhub.data.remote.request.AssignAdminRequest
import com.hakankuru.eventhub.data.remote.request.ClubCreateRequest
import com.hakankuru.eventhub.data.remote.response.AdminResponse
import com.hakankuru.eventhub.data.remote.response.AdminUserDto
import com.hakankuru.eventhub.data.remote.response.ClubResponse
import com.hakankuru.eventhub.domain.util.Result

interface SuperAdminRepository {
    suspend fun getAllClubs(): Result<List<ClubResponse>>
    suspend fun createClub(request: ClubCreateRequest): Result<ClubResponse>
    suspend fun updateClub(clubId: Long, request: ClubCreateRequest): Result<ClubResponse>
    suspend fun assignClubAdmin(clubId: Long, userEmail: String): Result<Unit>

    // ── SuperAdmin: üniversite-level admin yönetimi ─────────────────────────
    suspend fun assignAdmin(request: AssignAdminRequest): Result<AdminResponse>
    suspend fun removeAdmin(userId: Long): Result<Unit>
    suspend fun getAllAdmins(): Result<List<AdminResponse>>

    // ── SuperAdmin: tüm kullanıcıları listele ───────────────────────────────
    suspend fun getAllUsers(): Result<List<AdminUserDto>>
}
