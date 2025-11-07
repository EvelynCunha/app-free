package com.example.freela.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.freela.domain.usecase.ValidateBirthDateUseCase
import com.example.freela.domain.usecase.ValidateCpfUseCase
import com.example.freela.domain.usecase.ValidateEmailUseCase
import com.example.freela.domain.usecase.ValidateNameUseCase
import com.example.freela.domain.usecase.ValidatePhoneUseCase

class RegisterViewModelFactory(
    private val validateNameUseCase: ValidateNameUseCase,
    private val validateBirthDateUseCase: ValidateBirthDateUseCase,
    private val validateCpfUseCase: ValidateCpfUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePhoneUseCase: ValidatePhoneUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(validateNameUseCase, validateBirthDateUseCase, validateCpfUseCase,
                validateEmailUseCase, validatePhoneUseCase ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}