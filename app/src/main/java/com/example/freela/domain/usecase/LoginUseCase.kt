package com.example.freela.domain.usecase

import com.example.freela.repository.auth.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repository.login(email, password)
}