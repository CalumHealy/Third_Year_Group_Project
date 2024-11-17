package com.example.group_project.data

import com.example.group_project.model.StockResponse
import com.example.group_project.network.PolygonApiService

class StockRepository(private val apiService: PolygonApiService) {

    suspend fun getLiveStocks(date: String, apiKey: String): StockResponse {
        return apiService.getLiveStocks(date, apiKey)
    }
}
