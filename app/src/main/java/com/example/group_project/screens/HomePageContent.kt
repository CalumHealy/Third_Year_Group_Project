package com.example.group_project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.TrendingUp
import com.example.group_project.CryptoViewModel

@Composable
fun HomePageContent(navController: NavController) {
    // Make the whole screen scrollable to avoid cut-off content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Add top padding to avoid overlapping with status bar
        Spacer(modifier = Modifier.height(32.dp)) // Spacing at the top

        Text(
            text = "Welcome to ACT-Mobile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp)) // Spacing between sections

        Text(
            text = "Your Portfolio at a Glance:",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground
        )

        PortfolioSummary()  // Use the updated PortfolioSummary

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Quick Links:",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground
        )

        QuickLinks(navController)
    }
}

@Composable
fun PortfolioSummary(viewModel: CryptoViewModel = viewModel()) {
    val totalCryptoValue = viewModel.invested.sumOf { it.currentPrice }
    val totalStockValue = viewModel.stocks.sumOf { it.price }
    val totalValue = totalCryptoValue + totalStockValue

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally // Center the content horizontally
    ) {
        Text(
            text = "Total Value: $${"%.2f".format(totalValue)}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Stocks: $${"%.2f".format(totalStockValue)}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Crypto: $${"%.2f".format(totalCryptoValue)}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}

@Composable
fun QuickLinks(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.Center, // Center icons horizontally
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home Button
        IconButton(
            onClick = { navController.navigate("home") },
            modifier = Modifier.padding(8.dp) // Add padding between buttons
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Home",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Profile Button
        IconButton(
            onClick = { navController.navigate("profile") },
            modifier = Modifier.padding(8.dp) // Add padding between buttons
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Profile",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Investments Button
        IconButton(
            onClick = { navController.navigate("investments") },
            modifier = Modifier.padding(8.dp) // Add padding between buttons
        ) {
            Icon(
                imageVector = Icons.Filled.TrendingUp,
                contentDescription = "Investments",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
