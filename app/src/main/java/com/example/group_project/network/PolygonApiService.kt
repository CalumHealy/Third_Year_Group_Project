package com.example.group_project.network

import com.example.group_project.model.StockResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PolygonApiService {

    @GET("v1/open-close/{ticker}/{date}")
    suspend fun getLiveStocks(
        @Query("date") date: String,
        @Query("apiKey") apiKey: String
    ): StockResponse
}
