package com.hakankuru.eventhub.domain.repository

import com.hakankuru.eventhub.data.remote.request.UserUpdateRequest
import com.hakankuru.eventhub.data.remote.response.EventResponse
import com.hakankuru.eventhub.data.remote.response.UserProfileResponse
import com.hakankuru.eventhub.domain.util.Result

interface UserRepository {
    suspend fun getCurrentUser(): Result<UserProfileResponse>
    suspend fun updateCurrentUser(request: UserUpdateRequest): Result<UserProfileResponse>
    suspend fun getCurrentUserEvents(): Result<List<EventResponse>>
}
