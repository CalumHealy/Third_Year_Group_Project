package com.example.group_project.network

data class StockDataResponse(
    val ticker: String,
    val results: List<StockData>
)

data class StockData(
    val s: String?,  // Stock Symbol
    val n: String,   // Name (or fallback to symbol)
    val v: Double,   // Volume
    val o: Double,   // Open Price
    val c: Double,   // Close Price (Current Price)
    val h: Double,   // High Price
    val l: Double,   // Low Price
    val t: Long,     // Last Trade Time (timestamp)
    val marketCap: Double?,  // Market Cap
    val peRatio: Double?,    // P/E Ratio
    val fiftyTwoWeekHigh: Double?,  // 52-week High
    val fiftyTwoWeekLow: Double?,   // 52-week Low
    val dividendYield: Double?      // Dividend Yield
)
