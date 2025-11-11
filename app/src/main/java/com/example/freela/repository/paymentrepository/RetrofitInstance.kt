package com.example.freela.repository.paymentrepository


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val brasilApiRetrofit: BrasilApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://brasilapi.com.br/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BrasilApi::class.java)
    }
}