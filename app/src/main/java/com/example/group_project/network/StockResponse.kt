package com.example.group_project.network

data class StockCompanyResponse(
    val results: List<StockCompany>
)

data class StockCompany(
    val symbol: String,
    val name: String,
    val price: Double,  // Assuming this is the price field
    val description: String?  // Description of the stock company
)



