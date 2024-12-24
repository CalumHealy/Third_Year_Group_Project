package com.example.group_project.screens


data class Crypto(
    val id: String,
    val symbol: String,
    val currentPrice: Double,
    val marketCap: Double,
    val volume24h: Double,
    val priceChange24h: Double,
    val circulatingSupply: Double,
    val ath: Double,
    val atl: Double,
    val hashingAlgorithm: String?
)

