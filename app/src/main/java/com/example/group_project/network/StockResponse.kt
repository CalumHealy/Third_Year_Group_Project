package com.example.group_project.network

data class StockDataResponse(
    val ticker: String,
    val results: List<StockData>
)

data class StockData(
    val s: String?,
    val n: String, // name
    val v: Double,  // Volume
    val o: Double,  // Open Price
    val c: Double,  // Close Price (Current Price)
    val h: Double,  // High Price
    val l: Double,  // Low Price
    val t: Long,    // Last Trade Time (timestamp)
    val marketCap: Double?,  // Market Cap (not always available)
    val peRatio: Double?,    // P/E Ratio (not always available)
    val fiftyTwoWeekHigh: Double?,  // 52-week High (not always available)
    val fiftyTwoWeekLow: Double?,   // 52-week Low (not always available)
    val dividendYield: Double?  // Dividend Yield (if available)
)