package com.hakankuru.eventhub.domain.repository

import com.hakankuru.eventhub.data.remote.request.EventCreateRequest
import com.hakankuru.eventhub.data.remote.response.EventResponse
import com.hakankuru.eventhub.domain.util.Result

interface EventRepository {
    suspend fun getClubEvents(clubId: Long): Result<List<EventResponse>>
    suspend fun getAllEvents(): Result<List<EventResponse>>
    suspend fun createEvent(request: EventCreateRequest): Result<EventResponse>
    suspend fun joinEvent(eventId: Long): Result<Unit>
    suspend fun leaveEvent(eventId: Long): Result<Unit>
}
