package com.hakankuru.eventhub.data.remote.response

data class ClubMemberResponse(
    val userId: Long,
    val email: String,
    val name: String,
    val role: String,
    val startAt: String
)
