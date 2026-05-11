package com.hakankuru.eventhub.data.repository

import com.hakankuru.eventhub.data.remote.ApiService
import com.hakankuru.eventhub.data.remote.request.AuthResponse
import com.hakankuru.eventhub.data.remote.request.LoginRequest
import com.hakankuru.eventhub.data.remote.request.RegisterRequest
import com.hakankuru.eventhub.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResponse {
        return api.login(LoginRequest(email, password))
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        departmentId: Long
    ): AuthResponse {
        return api.register(
            RegisterRequest(name, email, password, departmentId)
        )
    }
}
