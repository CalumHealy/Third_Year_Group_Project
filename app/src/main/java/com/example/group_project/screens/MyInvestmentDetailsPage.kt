package com.example.group_project.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.group_project.CryptoViewModel
@Composable
fun InvestmentDetailsPage(name: String, symbol: String) {
    val viewModel: CryptoViewModel = viewModel()

    // Observe the list of cryptos and stocks from the ViewModel
    val cryptos = viewModel.cryptos
    val stocks = viewModel.stocks

    // Find the selected crypto or stock based on the symbol
    val selectedCrypto = cryptos.find { it.symbol == symbol }
    val selectedStock = stocks.find { it.s == symbol }

    // Show a loading spinner while data is loading
    if (cryptos.isEmpty() && stocks.isEmpty()) {
        Text(text = "Loading...", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Investment Details for $name",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Show the details for the selected crypto
        if (selectedCrypto != null) {
            Text(
                text = "Symbol: ${selectedCrypto.symbol}",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Price: $${"%.2f".format(selectedCrypto.currentPrice)} USD",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Market Cap: $${"%.2f".format(selectedCrypto.marketCap)} USD",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "24h Volume: $${"%.2f".format(selectedCrypto.volume24h)} USD",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Price Change 24h: $${"%.2f".format(selectedCrypto.priceChange24h)} USD",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Circulating Supply: ${"%.2f".format(selectedCrypto.circulatingSupply)} ${selectedCrypto.symbol}",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "All Time High (ATH): $${"%.2f".format(selectedCrypto.ath)} USD",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "All Time Low (ATL): $${"%.2f".format(selectedCrypto.atl)} USD",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hashing Algorithm: ${selectedCrypto.hashingAlgorithm}",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
        } else if (selectedStock != null) {
            // Show details for stock if available
            Text(
                text = "Name: ${selectedStock.n}",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Price: $${"%.2f".format(selectedStock.c)} USD",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Market Cap: $${"%.2f".format(selectedStock.marketCap)} USD", // If applicable
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "24h Volume: $${"%.2f".format(selectedStock.v)} USD", // If applicable
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            // Add any other stock-specific fields here
        } else {
            Text(
                text = "No data available for this investment.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
