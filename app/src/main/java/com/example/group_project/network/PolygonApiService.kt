package com.example.group_project.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PolygonApiService {
    @GET("v2/aggs/ticker/{ticker}/prev")
    suspend fun getStockAggregate(
        @retrofit2.http.Path("ticker") ticker: String,
        @Query("apiKey") apiKey: String
    ): Response<StockDataResponse>
}