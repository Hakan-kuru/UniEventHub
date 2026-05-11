package com.hakankuru.eventhub.domain.repository

import com.hakankuru.eventhub.data.remote.request.AuthResponse

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResponse
    suspend fun register(
        name: String,
        email: String,
        password: String,
        departmentId: Long
    ): AuthResponse
}
