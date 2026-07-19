package com.hakankuru.eventhub.data.remote.response

data class EventResponse(
    val eventId: Long,
    val title: String,
    val description: String,
    val image: String?,
    val startAt: String,
    val endAt: String?,
    val applyStartAt: String?,
    val applyEndAt: String?,
    val capacity: Int?,
    val clubId: Long,
    val clubName: String
)
