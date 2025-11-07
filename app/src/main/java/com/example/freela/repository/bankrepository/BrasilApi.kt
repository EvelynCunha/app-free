package com.example.freela.repository.bankrepository

import com.example.freela.repository.response.BankResponse
import retrofit2.Response
import retrofit2.http.GET

interface BrasilApi {

    // Lista de bancos
    @GET("banks/v1")
    suspend fun getBancos(): Response<List<BankResponse>>
}
