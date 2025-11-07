package com.example.freela.domain.usecase

import android.util.Patterns

class ValidateEmailUseCase {
    // Define a regra: Nome deve ter no mínimo 8 caracteres
    private val MIN_LENGTH = 8
    //Valida se o nome fornecido atende às regras de negócio
    operator fun invoke(nome: String): Boolean {
        // 1. Remove espaços em branco
        val trimmedName = nome.trim()

        // 2. Verifica se não está vazio E se tem o tamanho mínimo
        return !trimmedName.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(trimmedName).matches()
    }
}