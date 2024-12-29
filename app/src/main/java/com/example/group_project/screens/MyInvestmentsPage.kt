package com.example.group_project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.group_project.CryptoViewModel

@Composable
fun MyInvestmentsPage(navController: NavController) {
    val viewModel: CryptoViewModel = viewModel()
    val investedCryptos = viewModel.investedCryptos
    val investedStocks = viewModel.investedStocks

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Display invested cryptocurrencies
        items(investedCryptos) { crypto ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                InvestmentCard(
                    name = crypto.id.capitalize(),
                    symbol = crypto.symbol.uppercase(),
                    price = crypto.currentPrice,
                    marketCap = crypto.marketCap,
                    volume = crypto.volume24h,
                    onBuySellClick = {
                        if (viewModel.isInvestedInCrypto(crypto)) {
                            viewModel.removeFromInvestedCryptos(crypto)
                        } else {
                            viewModel.addToInvestedCryptos(crypto)
                        }
                    },
                    onDetailsClick = {
                        navController.navigate("investment_details/${crypto.id}/${crypto.symbol}")
                    }
                )
            }
        }

        // Display invested stocks
        items(investedStocks) { stock ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                InvestmentCard(
                    name = stock.name,
                    symbol = stock.ticker,
                    price = stock.price,
                    marketCap = stock.marketCap ?: 0.0,
                    volume = stock.volume24h ?: 0.0,
                    onBuySellClick = {
                        if (viewModel.isInvestedInStock(stock)) {
                            viewModel.removeFromInvestedStocks(stock)
                        } else {
                            viewModel.addToInvestedStocks(stock)
                        }
                    },
                    onDetailsClick = {
                        navController.navigate("investment_details/${stock.ticker}")
                    }
                )
            }
        }
    }
}



@Composable
fun InvestmentCard(
    name: String,
    symbol: String,
    price: Double,
    marketCap: Double,
    volume: Double,
    onBuySellClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Name and Symbol
            Text(
                text = name,
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Symbol: $symbol",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price, Market Cap, and Volume
            Text(
                text = "Price: $${"%.2f".format(price)}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Market Cap: $${"%.2f".format(marketCap)}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "24h Volume: $${"%.2f".format(volume)}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Buy/Sell Button
            Button(
                onClick = onBuySellClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ) {
                Text(text = "Sell")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details Button
            Button(
                onClick = onDetailsClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ) {
                Text(text = "Details")
            }
        }
    }
}
