package com.example.group_project.screens

import com.google.gson.annotations.SerializedName


data class Crypto(
    @SerializedName("current_price") val currentPrice: Double,
    @SerializedName("market_cap") val marketCap: Double,
    @SerializedName("total_volume") val volume24h: Double,
    @SerializedName("price_change_24h") val priceChange24h: Double,
    @SerializedName("circulating_supply") val circulatingSupply: Double,
    @SerializedName("ath") val ath: Double,
    @SerializedName("atl") val atl: Double,
    val id: String,
    val symbol: String,
    val hashingAlgorithm: String? = null
)


