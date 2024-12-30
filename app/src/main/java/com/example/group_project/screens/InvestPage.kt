package com.example.group_project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.navigation.NavController
import com.example.group_project.CryptoViewModel
import com.example.group_project.network.StockData

@Composable
fun InvestPage(navController: NavController) {
    val viewModel: CryptoViewModel = viewModel()

    val selectedTab = remember { mutableStateOf(0) } // 0 for Crypto, 1 for Stock

    val cryptos = viewModel.cryptos.take(30)
    val stocks = viewModel.stocks.take(50)

    Column(modifier = Modifier.fillMaxSize()) {
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selectedTab.value == 0) {
                items(cryptos) { crypto ->
                    CryptoItem(crypto = crypto, viewModel = viewModel, navController = navController)
                }
            } else {
                itemsIndexed(stocks) { index, stock ->
                    StockItem(stock = stock, viewModel = viewModel, navController = navController, index = index)
                }

            }
        }
    }
}

@Composable
fun CryptoItem(crypto: Crypto, viewModel: CryptoViewModel, navController: NavController) {
    val isInvested = viewModel.isInvestedInCrypto(crypto)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .height(100.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(text = crypto.id, fontSize = 20.sp)
            Text(text = crypto.symbol, fontSize = 16.sp)
            Text(text = "Price: $${"%.2f".format(crypto.currentPrice)}", fontSize = 16.sp)
        }

        Column(
            modifier = Modifier.width(120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (isInvested) {
                        viewModel.removeFromInvestedCryptos(crypto)
                    } else {
                        viewModel.addToInvestedCryptos(crypto)
                    }
                },
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
            ) {
                Text(text = if (isInvested) "Sell" else "Buy", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    navController.navigate("investment_details/${crypto.id}/${crypto.symbol}")
                },
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Details", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun StockItem(stock: StockData, viewModel: CryptoViewModel, navController: NavController, index: Int) {
    val isInvested = viewModel.isInvestedInStock(stock)

    // Manually set names for the first 10 stocks
    val manualNames = listOf(
        "Apple", "Google", "Microsoft", "Amazon", "Tesla",
        "Nvidia", "Meta", "Netflix", "AMD", "Disney"
    )

    // Check if the stock index is within the first 10 and manually assign the name
    val stockName = if (index < manualNames.size) manualNames[index] else stock.n

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .height(100.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(text = stockName, fontSize = 20.sp)
            Text(text = "Price: $${"%.2f".format(stock.c)}", fontSize = 16.sp)
        }

        Column(
            modifier = Modifier.width(120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (isInvested) {
                        viewModel.removeFromInvestedStocks(stock)
                    } else {
                        viewModel.addToInvestedStocks(stock)
                    }
                },
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
            ) {
                Text(text = if (isInvested) "Sell" else "Buy", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    navController.navigate("investment_details/${stockName}/${stock.c}")
                },
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Details", fontSize = 12.sp)
            }
        }
    }
}

