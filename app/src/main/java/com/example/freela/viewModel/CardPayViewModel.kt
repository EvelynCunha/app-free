package com.example.freela.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CardPayViewModel : ViewModel() {

    private val _cardType = MutableLiveData<String>()
    val cardType: LiveData<String> = _cardType
    private val _cardName = MutableLiveData<String>()
    val cardName: LiveData<String> = _cardName

    private val _cardNumber = MutableLiveData<String>()
    val cardNumber: LiveData<String> = _cardNumber

    private val _cardExpiry = MutableLiveData<String>()
    val cardExpiry: LiveData<String> = _cardExpiry

    private val _cardCvv = MutableLiveData<String>()
    val cardCvv: LiveData<String> = _cardCvv

    fun updateCardType(Type: String) = _cardType.postValue(Type)
    fun updateCardName(name: String) = _cardName.postValue(name)
    fun updateCardNumber(number: String) = _cardNumber.postValue(number)
    fun updateCardExpiry(expiry: String) = _cardExpiry.postValue(expiry)
    fun updateCardCvv(cvv: String) = _cardCvv.postValue(cvv)
}