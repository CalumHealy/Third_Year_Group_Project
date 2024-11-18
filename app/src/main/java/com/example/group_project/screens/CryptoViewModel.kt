package com.example.group_project

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group_project.network.RetrofitClient
import com.example.group_project.network.StockCompany
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CryptoViewModel : ViewModel() {

    private val _cryptos = MutableStateFlow<List<Crypto>>(emptyList())
    val cryptos: StateFlow<List<Crypto>> get() = _cryptos

    private val _stocks = MutableStateFlow<List<StockCompany>>(emptyList())
    val stocks: StateFlow<List<StockCompany>> get() = _stocks

    // Map for invested cryptos and stocks with amounts
    private val _investedCryptos = MutableStateFlow<Map<Crypto, Double>>(emptyMap())
    private val _investedStocks = MutableStateFlow<Map<StockCompany, Double>>(emptyMap())

    val investedCryptos: StateFlow<Map<Crypto, Double>> get() = _investedCryptos
    val investedStocks: StateFlow<Map<StockCompany, Double>> get() = _investedStocks

    // Flag to toggle between real data and dummy data
    private val useDummyData = true

    // Add to invested with amount tracking
    fun addToInvested(item: Any?, amount: Double) {
        when (item) {
            is Crypto -> {
                _investedCryptos.value = _investedCryptos.value.toMutableMap().apply {
                    val currentAmount = this[item] ?: 0.0
                    this[item] = currentAmount + amount
                }
            }
            is StockCompany -> {
                _investedStocks.value = _investedStocks.value.toMutableMap().apply {
                    val currentAmount = this[item] ?: 0.0
                    this[item] = currentAmount + amount
                }
            }
            else -> Log.e("CryptoViewModel", "Unknown type or null item")
        }
    }

    // Remove from invested (deletes the investment)
    fun removeFromInvested(item: Any?) {
        when (item) {
            is Crypto -> {
                _investedCryptos.value = _investedCryptos.value - item
            }
            is StockCompany -> {
                _investedStocks.value = _investedStocks.value - item
            }
        }
    }

    init {
        if (useDummyData) {
            loadDummyData()
        } else {
            fetchCryptos()
            fetchStockCompanies()
        }
    }

    private fun loadDummyData() {
        // Dummy Data for Cryptos
        _cryptos.value = listOf(
            Crypto("bitcoin", "Bitcoin", "BTC", 50000.0, "Popular cryptocurrency"),
            Crypto("ethereum", "Ethereum", "ETH", 3000.0, "Smart contract platform"),
            Crypto("dogecoin", "Dogecoin", "DOGE", 0.2, "Meme-based cryptocurrency")
        )

        // Dummy Data for Stocks
        _stocks.value = listOf(
            StockCompany("AAPL", "Apple Inc.", 150.0, "Tech company"),
            StockCompany("GOOGL", "Alphabet Inc.", 2800.0, "Google's parent company"),
            StockCompany("AMZN", "Amazon", 3400.0, "E-commerce giant")
        )

        // Dummy investments
        _investedCryptos.value = mapOf(
            Crypto("bitcoin", "Bitcoin", "BTC", 50000.0, "Popular cryptocurrency") to 2000.0,
            Crypto("ethereum", "Ethereum", "ETH", 3000.0, "Smart contract platform") to 1500.0
        )

        _investedStocks.value = mapOf(
            StockCompany("AAPL", "Apple Inc.", 150.0, "Tech company") to 5000.0,
            StockCompany("GOOGL", "Alphabet Inc.", 2800.0, "Google's parent company") to 10000.0
        )
    }

    private fun fetchCryptos() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.coinGeckoService.getCryptos()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _cryptos.value = it
                    }
                } else {
                    handleError("API Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                handleError("Network Error: ${e.localizedMessage}")
            }
        }
    }

    private fun fetchStockCompanies() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.polygonApiService.getStockCompanies("YOUR_API_KEY")
                if (response.isSuccessful) {
                    response.body()?.let {
                        // Successfully fetched stock companies
                        _stocks.value = it.results.take(50) // Get first 50 results
                        Log.d("CryptoViewModel", "Stock data fetched: ${it.results.size} items")
                    }
                } else {
                    handleError("API Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                handleError("Network Error: ${e.localizedMessage}")
            }
        }
    }

    fun getStockById(symbol: String?): StockCompany? {
        return _stocks.value.find { it.symbol == symbol }
    }

    private fun handleError(message: String) {
        Log.e("CryptoViewModel", message)
    }
}

