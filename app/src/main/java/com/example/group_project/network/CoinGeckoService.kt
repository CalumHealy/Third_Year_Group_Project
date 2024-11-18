package com.example.group_project.network

import com.example.group_project.Crypto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CoinGeckoService {

    @GET("coins/{id}")
    suspend fun getCryptoById(@Path("id") cryptoId: String): Response<CryptoDetails>

    @GET("coins/markets?vs_currency=usd")
    suspend fun getCryptos(): Response<List<Crypto>>
}