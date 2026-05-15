package com.hakankuru.eventhub.domain.repository

import com.hakankuru.eventhub.data.remote.request.ClubCreateRequest
import com.hakankuru.eventhub.data.remote.response.ClubResponse
import com.hakankuru.eventhub.domain.util.Result

interface SuperAdminRepository {
    suspend fun getAllClubs(): Result<List<ClubResponse>>
    suspend fun createClub(request: ClubCreateRequest): Result<ClubResponse>
    suspend fun updateClub(clubId: Long, request: ClubCreateRequest): Result<ClubResponse>
    suspend fun assignClubAdmin(clubId: Long, userEmail: String): Result<Unit>
}
