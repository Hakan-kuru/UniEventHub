package com.hakankuru.eventhub.data.remote.response

data class AdminResponse(
    val userId: Long,
    val email: String,
    val role: String,
    val universityId: Long?
)
