package com.example.freela.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freela.domain.usecase.ValidateBirthDateUseCase
import com.example.freela.domain.usecase.ValidateCpfUseCase
import com.example.freela.domain.usecase.ValidateEmailUseCase
import com.example.freela.domain.usecase.ValidateNameUseCase
import com.example.freela.domain.usecase.ValidatePhoneUseCase

class RegisterViewModel(
    private val validateNameUseCase: ValidateNameUseCase,
    private val validateBirthDateUseCase: ValidateBirthDateUseCase,
    private val validateCpfUseCase: ValidateCpfUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePhoneUseCase: ValidatePhoneUseCase
) : ViewModel() {
    private val _error = MutableLiveData<String>() //viewModel de ações

    val errorInput: LiveData<String> = _error //observable

    private val _allValid = MutableLiveData<Boolean>()
    val allValid: LiveData<Boolean> = _allValid

    val listError = mutableListOf<String>()
    fun isNameValid(name: String): Boolean = validateNameUseCase(name)
    fun isBirthDateValid(date: String): Boolean = validateBirthDateUseCase(date)
    fun isCpfValid(cpf: String): Boolean = validateCpfUseCase(cpf)
    fun isEmailValid(email: String): Boolean = validateEmailUseCase(email)
    fun isConfirmaEmailValid(confirmaEmail: String, email: String): Boolean {
        return confirmaEmail == email
    }
    fun isCheckboxValid(isChecked: Boolean): Boolean {
        return isChecked
    }
    fun isPhoneValid(phone: String): Boolean = validatePhoneUseCase(phone)

    fun isErrorValid(name: String, date: String, cpf: String, email: String, confirmaEmail:String, phone: String, isChecked: Boolean) {
        if (!isNameValid(name)) {
            listError.add("Nome")
        }
        if (!isBirthDateValid(date)) {
            listError.add("Data de Nascimento")
        }
        if (!isCpfValid(cpf)) {
            listError.add("Cpf")
        }
        if (!isEmailValid(email)) {
            listError.add("Email")
        }
        if(!isConfirmaEmailValid(confirmaEmail, email)){
            listError.add("Confirmar Email")
        }
        if (!isPhoneValid(phone)) {
            listError.add("Telefone")
        }
        if (!isCheckboxValid(isChecked)) {
            listError.add("Aceitar os termos")
        }
        if (listError.isEmpty()) {
            _allValid.postValue(true) // Sinaliza que TUDO está OK
        } else {
            errorStr() // Exibe o AlertDialog se houver erros
        }
    }

    fun errorStr() {
        var error = ""
        for (value in listError) {
            error += "${value}\n"
        }
        listError.clear()
        _error.postValue(error) // Avisa a view que teve uma ação
    }
}

