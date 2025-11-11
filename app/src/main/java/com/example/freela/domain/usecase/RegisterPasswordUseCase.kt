package com.example.freela.domain.usecase

import com.example.freela.repository.auth.AuthRepository

class RegisterPasswordUseCase(private val repository: AuthRepository) {

    companion object {
        private const val PASSWORDS_DONT_MATCH = "As senhas não conferem"
        private const val WEAK_PASSWORD = "Senha fraca"
    }

    private val passwordPattern = Regex("^(?=.*[A-Za-z])(?=.*\\d).{6,}$") // >=6 com letra+numero

    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String,
        name: String
    ): Result<Unit> {
        if (password != confirmPassword) {
            return Result.failure(Exception(PASSWORDS_DONT_MATCH))
        }
        if (!passwordPattern.matches(password)) {
            return Result.failure(Exception(WEAK_PASSWORD))
        }

        return repository.createUser(email, password, name)
    }
}
