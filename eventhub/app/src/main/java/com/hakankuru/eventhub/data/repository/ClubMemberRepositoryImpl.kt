package com.hakankuru.eventhub.data.repository

import com.hakankuru.eventhub.data.remote.ApiService
import com.hakankuru.eventhub.data.remote.request.AddClubMemberRequest
import com.hakankuru.eventhub.data.remote.response.ClubMemberManagementResponse
import com.hakankuru.eventhub.data.remote.response.UserSearchResponse
import com.hakankuru.eventhub.domain.repository.ClubMemberRepository
import com.hakankuru.eventhub.domain.util.Result
import javax.inject.Inject

class ClubMemberRepositoryImpl @Inject constructor(
    private val api: ApiService
) : ClubMemberRepository {

    override suspend fun getClubMembers(clubId: Long): Result<List<ClubMemberManagementResponse>> {
        return try {
            val response = api.getClubMembersMgmt(clubId)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Üyeler getirilirken bir hata oluştu")
        }
    }

    override suspend fun searchUsersForClub(clubId: Long, email: String): Result<List<UserSearchResponse>> {
        return try {
            val response = api.searchUsersForClub(clubId, email)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Kullanıcı aranırken bir hata oluştu")
        }
    }

    override suspend fun addMemberToClub(clubId: Long, email: String): Result<Unit> {
        return try {
            val response = api.addMemberToClub(clubId, AddClubMemberRequest(email))
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Üye eklenirken bir hata oluştu")
        }
    }

    override suspend fun removeMemberFromClub(clubId: Long, userId: Long): Result<Unit> {
        return try {
            val response = api.removeMemberFromClub(clubId, userId)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Üye silinirken bir hata oluştu")
        }
    }
}