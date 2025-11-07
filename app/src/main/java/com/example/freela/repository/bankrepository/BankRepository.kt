package com.example.freela.repository.bankrepository

import com.example.freela.repository.response.BankResponse

class BankRepository {
    private val brasilApi = RetrofitInstance.brasilApiRetrofit

    suspend fun seekBanks(): List<BankResponse>? {
        val response = brasilApi.getBancos()
        return if (response.isSuccessful) response.body() else null
    }
}
