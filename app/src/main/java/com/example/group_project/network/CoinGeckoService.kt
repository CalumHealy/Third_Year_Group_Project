package com.example.group_project.network

import com.example.group_project.screens.Crypto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoService {

    // Fetches detailed market information for cryptocurrencies
    @GET("coins/markets")
    suspend fun getCryptoDetails(
        @Query("ids") ids: String, // e.g., "bitcoin,ethereum,binancecoin"
        @Query("vs_currency") vsCurrency: String // e.g., "usd"
    ): Response<List<Crypto>>
}


