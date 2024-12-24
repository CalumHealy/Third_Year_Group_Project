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
import com.example.group_project.Crypto
import com.example.group_project.network.StockCompany

@Composable
fun InvestmentDetailsPage(name: String, symbol: String) {
    val viewModel: CryptoViewModel = viewModel()

    // Retrieve the live data for the selected crypto or stock
    val selectedCrypto = viewModel.cryptos.find { it.symbol == symbol }
    val selectedStock = viewModel.stocks.find { it.ticker == symbol }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Investment Details for $name",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Show the live price data for the selected crypto
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
        } else if (selectedStock != null) {
            // Show details for stock if available
            Text(
                text = "Symbol: ${selectedStock.ticker}",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Price: $${"%.2f".format(selectedStock.price)} USD",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Text(
                text = "No data available for this investment.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
