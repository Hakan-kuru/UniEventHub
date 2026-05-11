package com.hakankuru.eventhub.data.remote.request

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val departmentId: Long
)