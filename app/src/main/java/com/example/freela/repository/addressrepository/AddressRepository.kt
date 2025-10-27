package com.example.freela.repository.addressrepository

import com.example.freela.repository.response.ViaCepResponse
import com.example.freela.repository.response.CityResponse
import com.example.freela.repository.response.StateResponse

class AddressRepository {

    private val viaCepApi = RetrofitClient.viaCepRetrofit
    private val ibgeApi = RetrofitClient.ibgeRetrofit

    suspend fun buscarCep(cep: String): ViaCepResponse? {
        val response = viaCepApi.getEndereco(cep)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun buscarEstados(): List<StateResponse>? {
        val response = ibgeApi.getEstados()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun buscarCidades(uf: String): List<CityResponse>? {
        val response = ibgeApi.getCidades(uf)
        return if (response.isSuccessful) response.body() else null
    }
}

