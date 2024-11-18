package com.example.group_project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.group_project.CryptoViewModel




@Composable
fun MyInvestmentsPage() {
    // Get the viewModel
    val viewModel: CryptoViewModel = viewModel()

    // Collect invested cryptos and stocks as states
    val investedCryptos by viewModel.investedCryptos.collectAsState()
    val investedStocks by viewModel.investedStocks.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // Title for Cryptos section
        Text(text = "Cryptos", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(16.dp)) // Adding space between sections

        LazyColumn {
            items(investedCryptos.entries.toList()) { (crypto, amount) ->
                InvestmentCard(
                    name = crypto.name,
                    id = crypto.id,
                    amountInvested = amount,
                    price = crypto.price,
                    type = "Crypto"
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp)) // Adding space before Stocks section

        // Title for Stocks section
        Text(text = "Stocks", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(investedStocks.entries.toList()) { (stock, amount) ->
                InvestmentCard(
                    name = stock.name,
                    id = stock.symbol,
                    amountInvested = amount,
                    price = stock.price,
                    type = "Stock"
                )
            }
        }
    }
}

@Composable
fun InvestmentCard(name: String, id: String, amountInvested: Double, price: Double, type: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                text = "ID: $id",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp)) // Adding space before price and amount invested

            // Show price and amount invested
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Price: $${"%.2f".format(price)}",
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Invested: $${"%.2f".format(amountInvested)}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Add a "Sell" or "Buy" button depending on the investment status
            Spacer(modifier = Modifier.height(16.dp)) // Adding space before the button
            Button(
                onClick = { /* Handle buy/sell action */ },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = if (amountInvested > 0) "Sell" else "Buy")
            }
        }
    }
}