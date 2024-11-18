package com.example.group_project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.group_project.CryptoViewModel
import com.example.group_project.Crypto
import com.example.group_project.network.StockCompany

@Composable
fun InvestPage() {
    val viewModel: CryptoViewModel = viewModel()

    // Keep track of the selected tab (Crypto or Stock)
    val selectedTab = remember { mutableStateOf(0) }  // 0 for Crypto, 1 for Stock

    //Fetch only the first 30 cryptos and stocks
    val cryptos = viewModel.cryptos.take(30)
    val stocks = viewModel.stocks.take(50)

    Column(modifier = Modifier.fillMaxSize()) {
        // TabRow for selecting between Crypto and Stock
        TabRow(selectedTabIndex = selectedTab.value) {
            Tab(
                selected = selectedTab.value == 0,
                onClick = { selectedTab.value = 0 }
            ) {
                Text(text = "Crypto", modifier = Modifier.padding(16.dp))
            }
            Tab(
                selected = selectedTab.value == 1,
                onClick = { selectedTab.value = 1 }
            ) {
                Text(text = "Stock", modifier = Modifier.padding(16.dp))
            }
        }

        // LazyColumn to display either Crypto or Stock
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selectedTab.value == 0) {
                // Display Crypto items
                items(cryptos) { crypto ->
                    CryptoItem(crypto = crypto, viewModel = viewModel)
                }
            } else {
                // Display Stock items
                items(stocks) { stock ->
                    StockItem(stock = stock, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun CryptoItem(crypto: Crypto, viewModel: CryptoViewModel) {
    val isInvested = viewModel.isInvested(crypto)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(text = crypto.name, fontSize = 20.sp)
            Text(text = crypto.symbol, fontSize = 16.sp)
        }
        Button(
            onClick = {
                if (isInvested) {
                    viewModel.removeFromInvested(crypto)
                } else {
                    viewModel.addToInvested(crypto)
                }
            },
            modifier = Modifier.height(48.dp)
        ) {
            Text(text = if (isInvested) "Sell" else "Buy")
        }
    }
}

@Composable
fun StockItem(stock: StockCompany, viewModel: CryptoViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(text = stock.name, fontSize = 20.sp)
            Text(text = stock.ticker, fontSize = 16.sp)
        }
        Button(
            onClick = {
                // Handle buy/sell actions here
            },
            modifier = Modifier.height(48.dp)
        ) {
            Text(text = "Buy")  // Change to "Sell" if needed
        }
    }
}


