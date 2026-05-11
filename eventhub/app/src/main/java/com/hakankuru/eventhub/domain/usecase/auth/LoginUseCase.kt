package com.hakankuru.eventhub.domain.usecase.auth

import com.hakankuru.eventhub.data.remote.request.AuthResponse
import com.hakankuru.eventhub.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResponse {
        return repository.login(email, password)
    }
}
