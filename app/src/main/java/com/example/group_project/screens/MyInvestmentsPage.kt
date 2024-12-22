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
import com.example.group_project.CryptoViewModel
import com.example.group_project.Crypto
import com.example.group_project.network.StockCompany

@Composable
fun MyInvestmentsPage() {
    // Specify the ViewModel type to avoid the type inference issue
    val viewModel: CryptoViewModel = viewModel()

    // Get the list of invested items (Crypto and/or Stocks)
    val invested = viewModel.invested  // Assuming 'invested' is a collection (e.g., List<Crypto> or List<StockCompany>)

    // Display both cryptos and stocks as investments
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Display Cryptos Investments
        items(invested.toList()) { crypto ->
            // Each crypto/stock item should be in a Row or Column
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp), // Added vertical padding for spacing between items
                contentAlignment = Alignment.CenterStart
            ) {
                InvestmentCard(
                    name = crypto.name,
                    symbol = crypto.symbol,
                    price = crypto.price,
                    onBuySellClick = {
                        // Toggle investment status when Buy/Sell button is clicked
                        if (viewModel.isInvested(crypto)) {
                            viewModel.removeFromInvested(crypto)
                        } else {
                            viewModel.addToInvested(crypto)
                        }
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
    onBuySellClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Display name and symbol/id for crypto or stock
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

            Spacer(modifier = Modifier.height(8.dp)) // Adding space before price

            // Show price
            Text(
                text = "Price: $${"%.2f".format(price)}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )

            // Add a "Sell" or "Buy" button depending on the investment status
            Spacer(modifier = Modifier.height(16.dp)) // Adding space before the button
            Button(
                onClick = onBuySellClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ) {
                Text(text = "Sell")
            }
        }
    }
}
