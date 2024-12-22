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
    val invested = viewModel.invested

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(invested.toList()) { investment ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                InvestmentCard(
                    name = investment.name,
                    symbol = investment.symbol,
                    price = investment.currentPrice,
                    onBuySellClick = {
                        if (viewModel.isInvested(investment)) {
                            viewModel.removeFromInvested(investment)
                        } else {
                            viewModel.addToInvested(investment)
                        }
                    },
                    onDetailsClick = {
                        // Navigate to the details page and pass investment details
                        navController.navigate("investment_details/${investment.name}/${investment.symbol}")
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

            Spacer(modifier = Modifier.height(8.dp)) // Space before price
            Text(
                text = "Price: $${"%.2f".format(price)}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

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
