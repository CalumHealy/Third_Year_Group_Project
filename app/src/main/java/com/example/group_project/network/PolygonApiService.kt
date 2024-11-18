package com.example.group_project.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PolygonApiService {
    // Endpoint to fetch the list of stock companies (50 items per request)
    @GET("v3/reference/tickers")
    suspend fun getStockCompanies(
        @Query("apiKey") apiKey: String,
        @Query("limit") limit: Int = 50
    ): Response<StockCompanyResponse>
}
