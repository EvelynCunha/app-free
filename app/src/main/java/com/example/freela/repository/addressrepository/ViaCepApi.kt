package com.example.freela.repository.addressrepository

import com.example.freela.repository.response.ViaCepResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
interface ViaCepApi {
    @GET("{cep}/json/")
    suspend fun getEndereco(@Path("cep") cep: String): Response<ViaCepResponse>
}