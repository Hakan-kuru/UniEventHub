package com.hakankuru.eventhub.data.repository

import com.hakankuru.eventhub.data.remote.ApiService
import com.hakankuru.eventhub.data.remote.request.AssignAdminRequest
import com.hakankuru.eventhub.data.remote.request.ClubCreateRequest
import com.hakankuru.eventhub.data.remote.response.AdminResponse
import com.hakankuru.eventhub.data.remote.response.AdminUserDto
import com.hakankuru.eventhub.data.remote.response.ClubResponse
import com.hakankuru.eventhub.domain.repository.SuperAdminRepository
import com.hakankuru.eventhub.domain.util.Result
import javax.inject.Inject

class SuperAdminRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SuperAdminRepository {

    override suspend fun getAllClubs(): Result<List<ClubResponse>> {
        return try {
            val response = apiService.getAllClubs()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }

    override suspend fun createClub(request: ClubCreateRequest): Result<ClubResponse> {
        return try {
            val response = apiService.createClub(request)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }

    override suspend fun updateClub(clubId: Long, request: ClubCreateRequest): Result<ClubResponse> {
        return try {
            val response = apiService.updateClub(clubId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }

    override suspend fun assignClubAdmin(clubId: Long, userEmail: String): Result<Unit> {
        return try {
            val response = apiService.assignClubAdmin(clubId, userEmail)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }

    // ── SuperAdmin: üniversite-level admin yönetimi ─────────────────────────

    override suspend fun assignAdmin(request: AssignAdminRequest): Result<AdminResponse> {
        return try {
            val response = apiService.assignAdmin(request)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }

    override suspend fun removeAdmin(userId: Long): Result<Unit> {
        return try {
            val response = apiService.removeAdmin(userId)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }

    override suspend fun getAllAdmins(): Result<List<AdminResponse>> {
        return try {
            val response = apiService.getAllAdmins()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }

    override suspend fun getAllUsers(): Result<List<AdminUserDto>> {
        return try {
            val response = apiService.getAllUsers()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }
}
