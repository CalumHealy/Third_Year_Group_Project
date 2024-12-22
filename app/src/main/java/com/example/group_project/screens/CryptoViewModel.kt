package com.example.group_project

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group_project.network.RetrofitClient
import com.example.group_project.network.StockCompany
import com.example.group_project.screens.Crypto
import kotlinx.coroutines.launch

class CryptoViewModel : ViewModel() {

    private val _cryptos = mutableListOf<Crypto>()
    val cryptos: List<Crypto> get() = _cryptos

    private val _stocks = mutableListOf<StockCompany>()
    val stocks: List<StockCompany> get() = _stocks

    private val _invested = mutableSetOf<Crypto>()
    val invested: List<Crypto> get() = _invested.toList()

    fun isInvested(crypto: Crypto): Boolean = _invested.contains(crypto)

    fun addToInvested(crypto: Crypto) {
        _invested.add(crypto)
    }

    fun removeFromInvested(crypto: Crypto) {
        _invested.remove(crypto)
    }

    init {
        fetchCryptos()
        fetchStockCompanies()
    }

    private fun fetchCryptos() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.coinGeckoService.getCryptos()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _cryptos.clear()
                        _cryptos.addAll(it)
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
                val response = RetrofitClient.polygonApiService.getStockCompanies("ieZvxvRmzI_3GYjP7aSQc2yylFbr7Adq")
                if (response.isSuccessful) {
                    response.body()?.let {
                        _stocks.clear()
                        _stocks.addAll(it.results.take(50))
                    }
                } else {
                    handleError("API Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                handleError("Network Error: ${e.localizedMessage}")
            }
        }
    }

    private fun handleError(message: String) {
        Log.e("CryptoViewModel", message)
    }
}
