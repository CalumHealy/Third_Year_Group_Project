package com.example.group_project.network

import com.example.group_project.screens.Crypto
import retrofit2.Response
import retrofit2.http.GET

interface CoinGeckoService {

    @GET("coins/markets?vs_currency=usd")
    suspend fun getCryptos(): Response<List<Crypto>>
}