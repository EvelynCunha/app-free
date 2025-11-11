package com.example.freela.domain.usecase

import com.example.freela.repository.auth.AuthRepository

class GetUserNameUseCase (
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): String? {
        return authRepository.getCurrentUserName()
    }
}

