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
                // Requesting prices for multiple cryptocurrencies
                val response = RetrofitClient.coinGeckoService.getCryptoPrice(
                    ids = "bitcoin,ethereum,binancecoin,ripple,cardano,polkadot,solana,litecoin,chainlink,uniswap,vechain,stellar,dogecoin,usd-coin,terraaluna,shiba-inu,polygon,monero,tron,neo,cosmos,tezos,algorand,dash,ethereum-classic,link,qtum,vechain,axie-infinity,flow",
                    vsCurrencies = "usd"
                )

                if (response.isSuccessful) {
                    response.body()?.let { prices ->
                        _cryptos.clear()
                        _cryptos.addAll(
                            listOf(
                                Crypto("Bitcoin", "BTC", prices["bitcoin"]?.get("usd") ?: 0.0),
                                Crypto("Ethereum", "ETH", prices["ethereum"]?.get("usd") ?: 0.0),
                                Crypto("Binance Coin", "BNB", prices["binancecoin"]?.get("usd") ?: 0.0),
                                Crypto("Ripple", "XRP", prices["ripple"]?.get("usd") ?: 0.0),
                                Crypto("Cardano", "ADA", prices["cardano"]?.get("usd") ?: 0.0),
                                Crypto("Polkadot", "DOT", prices["polkadot"]?.get("usd") ?: 0.0),
                                Crypto("Solana", "SOL", prices["solana"]?.get("usd") ?: 0.0),
                                Crypto("Litecoin", "LTC", prices["litecoin"]?.get("usd") ?: 0.0),
                                Crypto("Chainlink", "LINK", prices["chainlink"]?.get("usd") ?: 0.0),
                                Crypto("Uniswap", "UNI", prices["uniswap"]?.get("usd") ?: 0.0),
                                Crypto("VeChain", "VET", prices["vechain"]?.get("usd") ?: 0.0),
                                Crypto("Stellar", "XLM", prices["stellar"]?.get("usd") ?: 0.0),
                                Crypto("Dogecoin", "DOGE", prices["dogecoin"]?.get("usd") ?: 0.0),
                                Crypto("USD Coin", "USDC", prices["usd-coin"]?.get("usd") ?: 0.0),
                                Crypto("Terra Luna", "LUNA", prices["terraaluna"]?.get("usd") ?: 0.0),
                                Crypto("Shiba Inu", "SHIB", prices["shiba-inu"]?.get("usd") ?: 0.0),
                                Crypto("Polygon", "MATIC", prices["polygon"]?.get("usd") ?: 0.0),
                                Crypto("Monero", "XMR", prices["monero"]?.get("usd") ?: 0.0),
                                Crypto("Tron", "TRX", prices["tron"]?.get("usd") ?: 0.0),
                                Crypto("NEO", "NEO", prices["neo"]?.get("usd") ?: 0.0),
                                Crypto("Cosmos", "ATOM", prices["cosmos"]?.get("usd") ?: 0.0),
                                Crypto("Tezos", "XTZ", prices["tezos"]?.get("usd") ?: 0.0),
                                Crypto("Algorand", "ALGO", prices["algorand"]?.get("usd") ?: 0.0),
                                Crypto("Dash", "DASH", prices["dash"]?.get("usd") ?: 0.0),
                                Crypto("Ethereum Classic", "ETC", prices["ethereum-classic"]?.get("usd") ?: 0.0),
                                Crypto("Qtum", "QTUM", prices["qtum"]?.get("usd") ?: 0.0),
                                Crypto("Axie Infinity", "AXS", prices["axie-infinity"]?.get("usd") ?: 0.0),
                                Crypto("Flow", "FLOW", prices["flow"]?.get("usd") ?: 0.0)
                            )
                        )
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
                // Requesting stock data for multiple companies
                val response = RetrofitClient.polygonApiService.getStockCompanies(
                    apiKey = "ieZvxvRmzI_3GYjP7aSQc2yylFbr7Adq",
                    symbols = "AAPL,GOOG,MSFT,AMZN,TSLA,NVDA,FB,INTC,AMD,SPY,BA,DIS,GE,IBM,C,GM,COIN,UBER,LYFT,NFLX,MS,PYPL,BA,MRK,PFE,WMT,VZ,T,GS,JPM,INTU"
                )

                if (response.isSuccessful) {
                    response.body()?.let {
                        _stocks.clear()
                        _stocks.addAll(it.results.take(50)) // Add a list of stock companies, limit it if necessary
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
