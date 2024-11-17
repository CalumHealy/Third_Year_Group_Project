package com.example.group_project.screens

import StockViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.group_project.model.Stock
import com.example.group_project.BuildConfig


@Composable
fun LiveStockPage(stockViewModel: StockViewModel = viewModel()) {
    val stocks by stockViewModel.stocks.collectAsState()
    val apiKey = BuildConfig.POLYGON_API_KEY
    val date = "2023-11-16"

    // Fetch stocks on UI initialization
    LaunchedEffect(Unit) {
        stockViewModel.fetchLiveStocks(date, apiKey)
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(stocks) { stock ->
            StockRow(stock = stock, onAddClick = { stockViewModel.addToInvestments(stock) })
        }
    }
}


@Composable
fun StockRow(stock: Stock, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = stock.ticker, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Price: ${stock.close}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Volume: ${stock.volume}", style = MaterialTheme.typography.bodySmall)
        }
        Button(onClick = onAddClick) {
            Text("Add")
        }
    }
}
