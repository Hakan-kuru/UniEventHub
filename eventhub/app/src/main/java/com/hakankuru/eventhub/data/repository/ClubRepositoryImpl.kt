package com.hakankuru.eventhub.data.repository

import com.hakankuru.eventhub.data.remote.ApiService
import com.hakankuru.eventhub.data.remote.response.ClubMemberResponse
import com.hakankuru.eventhub.data.remote.response.ClubResponse
import com.hakankuru.eventhub.domain.repository.ClubRepository
import com.hakankuru.eventhub.domain.util.Result
import javax.inject.Inject

class ClubRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ClubRepository {

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

    override suspend fun getClubMembers(clubId: Long): Result<List<ClubMemberResponse>> {
        return try {
            val response = apiService.getClubMembers(clubId)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }

    override suspend fun addMember(clubId: Long, userEmail: String, role: String): Result<Unit> {
        return try {
            val response = apiService.addMember(clubId, userEmail, role)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }

    override suspend fun removeMember(clubId: Long, userId: Long): Result<Unit> {
        return try {
            val response = apiService.removeMember(clubId, userId)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }
}
