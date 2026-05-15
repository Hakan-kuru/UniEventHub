package com.hakankuru.eventhub.data.repository

import com.hakankuru.eventhub.data.remote.ApiService
import com.hakankuru.eventhub.data.remote.request.UserUpdateRequest
import com.hakankuru.eventhub.data.remote.response.EventResponse
import com.hakankuru.eventhub.data.remote.response.UserProfileResponse
import com.hakankuru.eventhub.domain.repository.UserRepository
import com.hakankuru.eventhub.domain.util.Result
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {

    override suspend fun getCurrentUser(): Result<UserProfileResponse> {
        return try {
            val response = apiService.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }

    override suspend fun updateCurrentUser(request: UserUpdateRequest): Result<UserProfileResponse> {
        return try {
            val response = apiService.updateCurrentUser(request)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }

    override suspend fun getCurrentUserEvents(): Result<List<EventResponse>> {
        return try {
            val response = apiService.getCurrentUserEvents()
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
