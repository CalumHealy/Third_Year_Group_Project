package com.example.group_project.screens

import StockViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.group_project.network.PolygonApiService
import com.example.group_project.ui.theme.ViewModel.StockViewModelFactory
import com.example.group_project.data.StockRepository
import com.example.group_project.model.Stock
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun InvestPage() {
    // Create Retrofit instance for PolygonApiService
    val apiService = Retrofit.Builder()
        .baseUrl("https://api.polygon.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PolygonApiService::class.java)

    // Create the StockRepository
    val repository = StockRepository(apiService)

    // Create the StockViewModel using the ViewModelFactory
    val stockViewModel: StockViewModel = viewModel(factory = StockViewModelFactory(repository))

    // Collect the investments from the ViewModel
    val investments by stockViewModel.investments.collectAsState()

    // Call to fetch live stocks
    LaunchedEffect(Unit) {
        stockViewModel.fetchLiveStocks("2023-11-16", "ieZvxvRmzI_3GYjP7aSQc2yylFbr7Adq") // Use your actual API key here
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (investments.isEmpty()) {
            // Show a loading spinner while fetching data
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Fetching the latest stocks...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        } else {
            // Show the list of stocks
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(investments) { stock ->
                    StockItem(
                        stock = stock,
                        onAddToPortfolio = { stockViewModel.addToInvestments(stock) }
                    )
                }
            }
        }
    }
}

@Composable
fun StockItem(stock: Stock, onAddToPortfolio: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stock.ticker,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Price: ${stock.close} USD",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Volume: ${stock.volume}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Add to Portfolio Button
            Button(
                onClick = onAddToPortfolio,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(text = "Add to Portfolio")
            }
        }
    }
}


