package com.example.group_project.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.group_project.CryptoViewModel

@Composable
fun MyInvestmentsPage() {
    val viewModel: CryptoViewModel = viewModel()
    val favorites = viewModel.favorites

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(favorites) { crypto ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${crypto.name} (${crypto.symbol})",
                    fontSize = 22.sp
                )
            }
        }
    }
}
