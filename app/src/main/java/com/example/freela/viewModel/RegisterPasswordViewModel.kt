package com.example.freela.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freela.domain.usecase.RegisterPasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegisterPasswordState {
    object Idle : RegisterPasswordState()
    object Loading : RegisterPasswordState()
    object Success : RegisterPasswordState()
    data class Error(val message: String) : RegisterPasswordState()
}

class RegisterPasswordViewModel(private val registerUseCase: RegisterPasswordUseCase) : ViewModel() {

    private val _state = MutableStateFlow<RegisterPasswordState>(RegisterPasswordState.Idle)
    val state: StateFlow<RegisterPasswordState> = _state

    fun register(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _state.value = RegisterPasswordState.Loading
            try {
                val result = registerUseCase(email, password, confirmPassword)
                if (result.isSuccess) {
                    _state.value = RegisterPasswordState.Success
                } else {
                    val ex = result.exceptionOrNull()
                    _state.value = RegisterPasswordState.Error(ex?.message ?: "Erro inesperado")
                }
            } catch (e: Exception) {
                _state.value = RegisterPasswordState.Error(e.message ?: "Erro inesperado")
            }
        }
    }

    fun resetState() {
        _state.value = RegisterPasswordState.Idle
    }
}
