package com.hakankuru.eventhub.domain.usecase.auth

import com.hakankuru.eventhub.data.remote.request.AuthResponse
import com.hakankuru.eventhub.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        departmentId: Long
    ): AuthResponse {
        return repository.register(name, email, password, departmentId)
    }
}
