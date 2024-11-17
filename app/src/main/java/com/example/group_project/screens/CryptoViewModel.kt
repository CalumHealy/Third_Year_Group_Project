package com.example.group_project

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group_project.network.RetrofitClient
import kotlinx.coroutines.launch

class CryptoViewModel : ViewModel() {

    private val _cryptos = mutableListOf<Crypto>()
    val cryptos: List<Crypto> get() = _cryptos

    // Placeholder function for invested
    private val _invested = mutableSetOf<Crypto>()
    val invested: Set<Crypto> get() = _invested
    fun isInvested(crypto: Crypto): Boolean = _invested.contains(crypto)

    // Add crypto to invested
    fun addToInvested(crypto: Crypto) {
        _invested.add(crypto)
    }

    // Remove crypto from invested
    fun removeFromInvested(crypto: Crypto) {
        _invested.remove(crypto)
    }

    init {
        fetchCryptos()
    }

    // Fetch cryptos from CoinGecko
    private fun fetchCryptos(){
        viewModelScope.launch {
            try{
                val response = RetrofitClient.coinGeckoService.getCryptos()
                if(response.isSuccessful) {
                    response.body()?.let {
                        _cryptos.clear()
                        _cryptos.addAll(it)
                    }
                } else {
                    // Handle non-2xx HTTP status codes
                    handleError("API Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception){
                // Handle network failure (e.g., no internet, timeout)
                handleError("Network Error: ${e.localizedMessage}")
            }
        }
    }
}

private fun handleError(message: String) {
    // Log the error message (use Timber or Log for better logging in real-world apps)
    Log.e("CryptoViewModel", message)
}
