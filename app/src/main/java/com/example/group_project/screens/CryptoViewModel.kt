package com.example.group_project

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group_project.network.RetrofitClient
import com.example.group_project.network.StockData
import com.example.group_project.screens.Crypto
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class CryptoViewModel : ViewModel() {

    private val _cryptos = mutableStateListOf<Crypto>() // Crypto list
    val cryptos: List<Crypto> get() = _cryptos

    private val _stocks = mutableStateListOf<StockData>() // Stock list
    val stocks: List<StockData> get() = _stocks

    private val _investedCryptos = mutableSetOf<Crypto>() // User's invested cryptos
    val investedCryptos: List<Crypto> get() = _investedCryptos.toList()

    private val _investedStocks = mutableSetOf<StockData>() // User's invested stocks
    val investedStocks: List<StockData> get() = _investedStocks.toList()

    // Manual stock names mapping
    private val stockNameMap = mapOf(
        "AAPL" to "Apple Inc.",
        "GOOG" to "Alphabet Inc.",
        "MSFT" to "Microsoft Corporation",
        "AMZN" to "Amazon.com, Inc.",
        "TSLA" to "Tesla, Inc.",
        "NVDA" to "NVIDIA Corporation",
        "FB" to "Meta Platforms, Inc."
    )

    // Method to get stock name from symbol
    fun getStockName(symbol: String): String = stockNameMap[symbol] ?: "Unknown Stock"

    // Methods for managing crypto investments
    fun isInvestedInCrypto(crypto: Crypto): Boolean = _investedCryptos.contains(crypto)
    fun addToInvestedCryptos(crypto: Crypto) {
        _investedCryptos.add(crypto)
    }
    fun removeFromInvestedCryptos(crypto: Crypto) {
        _investedCryptos.remove(crypto)
    }

    // Methods for managing stock investments
    fun isInvestedInStock(stock: StockData): Boolean = _investedStocks.contains(stock)
    fun addToInvestedStocks(stock: StockData) {
        _investedStocks.add(stock)
    }
    fun removeFromInvestedStocks(stock: StockData) {
        _investedStocks.remove(stock)
    }

    init {
        fetchCryptos()
        fetchStockCompanies()
    }

    private fun fetchCryptos() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.coinGeckoService.getCryptoDetails(
                    ids = "bitcoin,ethereum,binancecoin,ripple,cardano,dogecoin,solana,polkadot,tron,shiba-inu,polygon,uniswap,chainlink,cosmos,monero,stellar,vechain,tezos,theta,flow,apecoin",
                    vsCurrency = "usd"
                )

                if (response.isSuccessful) {
                    response.body()?.forEach { crypto ->
                        _cryptos.add(
                            Crypto(
                                id = crypto.id.capitalize(),
                                symbol = crypto.symbol.uppercase(),
                                currentPrice = crypto.currentPrice,
                                marketCap = crypto.marketCap,
                                volume24h = crypto.volume24h,
                                priceChange24h = crypto.priceChange24h,
                                circulatingSupply = crypto.circulatingSupply,
                                ath = crypto.ath,
                                atl = crypto.atl,
                                hashingAlgorithm = crypto.hashingAlgorithm ?: "N/A"
                            )
                        )
                    }
                } else {
                    handleError("Crypto API Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                handleError("Crypto Fetch Error: ${e.localizedMessage}")
            }
        }
    }

    private fun fetchStockCompanies() {
        val stockSymbols = listOf("AAPL", "GOOG", "MSFT", "AMZN", "TSLA", "NVDA", "FB")

        viewModelScope.launch {
            val stockResponses = stockSymbols.map { symbol ->
                async {
                    RetrofitClient.polygonApiService.getStockAggregate(symbol, "ieZvxvRmzI_3GYjP7aSQc2yylFbr7Adq")
                }
            }

            val responses = stockResponses.awaitAll()
            stockSymbols.forEachIndexed { index, symbol ->
                val response = responses[index]
                if (response.isSuccessful) {
                    response.body()?.results?.let { results ->
                        results.forEach { stock ->
                            _stocks.add(
                                StockData(
                                    s = symbol, // Stock Symbol
                                    n = stock.n.ifEmpty { symbol }, // Use stock name or fallback to symbol
                                    v = stock.v, // Volume
                                    o = stock.o, // Open Price
                                    c = stock.c, // Close Price (Current Price)
                                    h = stock.h, // High Price
                                    l = stock.l, // Low Price
                                    t = stock.t, // Timestamp
                                    marketCap = stock.marketCap,
                                    peRatio = stock.peRatio,
                                    fiftyTwoWeekHigh = stock.fiftyTwoWeekHigh,
                                    fiftyTwoWeekLow = stock.fiftyTwoWeekLow,
                                    dividendYield = stock.dividendYield
                                )
                            )
                        }
                    }
                } else {
                    handleError("Stock API Error: ${response.code()} - ${response.message()}")
                }
            }
        }
    }


    private fun handleError(message: String) {
        Log.e("CryptoViewModel", message)
    }
}
