package com.example.group_project

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group_project.network.RetrofitClient
import com.example.group_project.network.StockCompany
import com.example.group_project.screens.Crypto
import kotlinx.coroutines.launch

class CryptoViewModel : ViewModel() {

    private val _cryptos = mutableStateListOf<Crypto>() // Changed to mutableStateListOf
    val cryptos: List<Crypto> get() = _cryptos

    private val _stocks = mutableStateListOf<StockCompany>() // Changed to mutableStateListOf
    val stocks: List<StockCompany> get() = _stocks

    private val _investedCryptos = mutableSetOf<Crypto>()
    val investedCryptos: List<Crypto> get() = _investedCryptos.toList()

    private val _investedStocks = mutableSetOf<StockCompany>()
    val investedStocks: List<StockCompany> get() = _investedStocks.toList()

    // Crypto investment methods
    fun isInvestedInCrypto(crypto: Crypto): Boolean = _investedCryptos.contains(crypto)

    fun addToInvestedCryptos(crypto: Crypto) {
        _investedCryptos.add(crypto)
    }

    fun removeFromInvestedCryptos(crypto: Crypto) {
        _investedCryptos.remove(crypto)
    }

    // Stock investment methods
    fun isInvestedInStock(stock: StockCompany): Boolean = _investedStocks.contains(stock)

    fun addToInvestedStocks(stock: StockCompany) {
        _investedStocks.add(stock)
    }

    fun removeFromInvestedStocks(stock: StockCompany) {
        _investedStocks.remove(stock)
    }

    init {
        fetchCryptos()
        fetchStockCompanies()
    }

    private fun fetchCryptos() {
        viewModelScope.launch {
            try {
                // Requesting the details of multiple cryptocurrencies
                val response = RetrofitClient.coinGeckoService.getCryptoDetails(
                    ids = "bitcoin,ethereum,binancecoin,ripple,cardano,dogecoin,solana,polkadot,tron,shiba-inu,polygon,uniswap,chainlink,cosmos,monero,stellar,vechain,tezos,theta,flow,apecoin",
                    vsCurrency = "usd"
                )

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("API Response", "Full API Response: $responseBody") // Log full response

                    responseBody?.let { cryptoList ->
                        Log.d("API Response", "Number of Cryptos fetched: ${cryptoList.size}") // Check how many cryptos were fetched

                        cryptoList.forEach { crypto ->
                            // Add crypto data to the list
                            _cryptos.add(Crypto(
                                id = crypto.id.capitalize(),
                                symbol = crypto.symbol.toUpperCase(),
                                currentPrice = crypto.currentPrice,
                                marketCap = crypto.marketCap,
                                volume24h = crypto.volume24h,
                                priceChange24h = crypto.priceChange24h,
                                circulatingSupply = crypto.circulatingSupply,
                                ath = crypto.ath,
                                atl = crypto.atl,
                                hashingAlgorithm = crypto.hashingAlgorithm ?: "N/A"
                            ))
                        }
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
