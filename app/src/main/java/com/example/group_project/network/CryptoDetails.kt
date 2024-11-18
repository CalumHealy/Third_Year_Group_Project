package com.example.group_project.network

data class CryptoDetails(
    val id: String,
    val name: String,
    val symbol: String,
    val description: Description,
    val market_data: MarketData
)

data class Description(
    val en: String
)

data class MarketData(
    val current_price: Map<String, Float>
)
