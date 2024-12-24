package com.example.group_project.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoService {

    // Fetches the price for a list of cryptocurrencies
    @GET("simple/price")
    suspend fun getCryptoPrice(
        @Query("ids") ids: String, // e.g., "bitcoin,ethereum,binancecoin"
        @Query("vs_currencies") vsCurrencies: String // e.g., "usd"
    ): Response<Map<String, Map<String, Double>>>
}

