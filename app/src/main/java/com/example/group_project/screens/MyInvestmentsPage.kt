package com.example.group_project.screens

import androidx.compose.foundation.layout.*
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
    val invested =viewModel.invested

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(invested.toList()) { crypto ->
            // Each crypto item should be in a Row or Column
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp), // Added vertical padding for spacing between items
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "${crypto.name} (${crypto.symbol})",
                    fontSize = 18.sp // Slightly smaller font size for readability
                )
            }
        }
    }
}
