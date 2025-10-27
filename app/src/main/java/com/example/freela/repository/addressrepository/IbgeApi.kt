package com.example.freela.repository.addressrepository

import com.example.freela.repository.response.StateResponse
import com.example.freela.repository.response.CityResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface IbgeApi {
    @GET("estados")
    suspend fun getEstados(): Response<List<StateResponse>>

    @GET("estados/{uf}/municipios")
    suspend fun getCidades(@Path("uf") uf: String): Response<List<CityResponse>>
}