package com.hakankuru.eventhub.data.repository

import com.hakankuru.eventhub.data.remote.ApiService
import com.hakankuru.eventhub.data.remote.request.EventCreateRequest
import com.hakankuru.eventhub.data.remote.response.EventResponse
import com.hakankuru.eventhub.domain.repository.EventRepository
import com.hakankuru.eventhub.domain.util.Result
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : EventRepository {

    override suspend fun getClubEvents(clubId: Long): Result<List<EventResponse>> {
        return try {
            val response = apiService.getClubEvents(clubId)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }

    override suspend fun getAllEvents(): Result<List<EventResponse>> {
        return try {
            val response = apiService.getAllEvents()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }

    override suspend fun createEvent(request: EventCreateRequest): Result<EventResponse> {
        return try {
            val response = apiService.createEvent(request)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }

    override suspend fun joinEvent(eventId: Long): Result<Unit> {
        return try {
            val response = apiService.joinEvent(eventId)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("Hata: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
        }
    }

    override suspend fun leaveEvent(eventId: Long): Result<Unit> {
        return try {
            val response = apiService.leaveEvent(eventId)
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
