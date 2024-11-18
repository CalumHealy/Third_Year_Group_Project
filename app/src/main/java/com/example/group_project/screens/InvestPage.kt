package com.example.group_project.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.group_project.CryptoViewModel
import com.example.group_project.Crypto
import com.example.group_project.network.StockCompany



@Composable
fun InvestPage(navController: NavController) {
    val viewModel: CryptoViewModel = viewModel()

    // Collect StateFlow values
    val cryptos by viewModel.cryptos.collectAsState()
    val stocks by viewModel.stocks.collectAsState()
    val investedCryptos by viewModel.investedCryptos.collectAsState()
    val investedStocks by viewModel.investedStocks.collectAsState()

    // State for selected tab
    val selectedTab = remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        // TabRow for selection
        TabRow(selectedTabIndex = selectedTab.value) {
            Tab(selected = selectedTab.value == 0, onClick = { selectedTab.value = 0 }) {
                Text(text = "Crypto", modifier = Modifier.padding(16.dp))
            }
            Tab(selected = selectedTab.value == 1, onClick = { selectedTab.value = 1 }) {
                Text(text = "Stock", modifier = Modifier.padding(16.dp))
            }
        }

        // Display items in LazyColumn
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selectedTab.value == 0) {
                items(cryptos) { crypto ->
                    val amountInvested = investedCryptos[crypto] ?: 0.0
                    CryptoItem(crypto = crypto, amountInvested = amountInvested) {
                        navController.navigate("cryptoDetail/${crypto.id}")
                    }
                }
            } else {
                items(stocks) { stock ->
                    val amountInvested = investedStocks[stock] ?: 0.0
                    StockItem(stock = stock, amountInvested = amountInvested) {
                        navController.navigate("stockDetail/${stock.symbol}")
                    }
                }
            }
        }
    }
}


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CryptoItem(crypto: Crypto, amountInvested: Double, onClick: () -> Unit) {
    // Check if the crypto is invested
    val isInvested = amountInvested > 0

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 16.dp).height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(text = crypto.name, fontSize = 20.sp)
            Text(text = crypto.symbol, fontSize = 16.sp)
        }
        Button(onClick = { onClick() }, modifier = Modifier.height(48.dp)) {
            Text(text = if (isInvested) "Sell" else "Buy")
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun StockItem(stock: StockCompany, amountInvested: Double, onClick: () -> Unit) {
    // Check if the stock is invested
    val isInvested = amountInvested > 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(text = stock.name, fontSize = 20.sp)
            Text(text = stock.symbol, fontSize = 16.sp)
            Text(text = "Price: $${stock.price}", fontSize = 14.sp)  // Display price
        }
        Button(onClick = { onClick() }, modifier = Modifier.height(48.dp)) {
            Text(text = if (isInvested) "Sell" else "Buy")
        }

    }

}

