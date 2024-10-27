
package com.example.group_project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.group_project.CryptoViewModel
import com.example.group_project.Crypto
@Composable
fun InvestPage() {
    val viewModel: CryptoViewModel = viewModel()

    // Sample cryptocurrency data
    val cryptos = listOf(
        Crypto("1", "Bitcoin", "BTC"),
        Crypto("2", "Ethereum", "ETH"),
        Crypto("3", "Ripple", "XRP"),
        Crypto("4", "Litecoin", "LTC")
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(cryptos) { crypto ->
            CryptoItem(crypto = crypto, viewModel = viewModel)
        }
    }
}

@Composable
fun CryptoItem(crypto: Crypto, viewModel: CryptoViewModel) {
    val isFavorite = viewModel.isFavorite(crypto)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = crypto.name, fontSize = 20.sp)
            Text(text = crypto.symbol, fontSize = 16.sp)
        }
        Button(onClick = {
            if (isFavorite) {
                viewModel.removeFromFavorites(crypto)
            } else {
                viewModel.addToFavorites(crypto)
            }
        }) {
            Text(text = if (isFavorite) "Sell" else "Buy")
        }
    }
}

