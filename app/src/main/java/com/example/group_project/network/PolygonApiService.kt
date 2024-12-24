package com.example.group_project.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PolygonApiService {

    // Fetches stock data for multiple companies
    @GET("v3/reference/tickers")
    suspend fun getStockCompanies(
        @Query("apiKey") apiKey: String, // Your Polygon.io API key
        @Query("symbols") symbols: String // e.g., "AAPL,GOOG,MSFT"
    ): Response<StockResponse>
}
