package com.example.freela.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.freela.domain.usecase.RegisterPasswordUseCase

class RegisterPasswordViewModelFactory(
    private val registerPasswordUseCase: RegisterPasswordUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterPasswordViewModel::class.java)) {
            return RegisterPasswordViewModel(registerPasswordUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
