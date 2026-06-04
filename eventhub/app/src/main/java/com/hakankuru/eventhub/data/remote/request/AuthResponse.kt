package com.hakankuru.eventhub.data.remote.request

data class AuthResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val token: String? = null,
    val role: String? = null   // Backend'den gelen GlobalRole (SUPER_ADMIN, USER vb.)
)