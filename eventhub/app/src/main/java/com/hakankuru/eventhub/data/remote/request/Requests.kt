package com.hakankuru.eventhub.data.remote.request

data class ClubCreateRequest(
    val name: String,
    val description: String
)

data class EventCreateRequest(
    val clubId: Long,
    val title: String,
    val description: String,
    val startAt: String? = null,
    val endAt: String? = null,
    val capacity: Int? = null
)
