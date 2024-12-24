package com.example.group_project.network

data class StockResponse(
    val results: List<StockCompany>
)

data class StockCompany(
    val ticker: String,
    val name: String,
    val price: Double
)

