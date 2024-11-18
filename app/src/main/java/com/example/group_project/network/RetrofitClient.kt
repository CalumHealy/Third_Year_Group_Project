package com.example.group_project.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Base URL set up
    private const val BASE_URL_CRYPTO = "https://api.coingecko.com/api/v3/"
    private const val BASE_URL_STOCK = "https://api.polygon.io/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_CRYPTO)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // CoinGecko Connection
    val coinGeckoService: CoinGeckoService = retrofit.create(CoinGeckoService::class.java)

    // Polygon Connection
    val polygonApiService: PolygonApiService by lazy {
        val client = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_STOCK)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(PolygonApiService::class.java)
    }
}
