package com.hakankuru.eventhub.data.remote.response

data class UserSearchResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val departmentName: String?
)