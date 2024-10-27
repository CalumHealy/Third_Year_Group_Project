package com.example.group_project.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.group_project.wallet.WalletViewModel

@Composable
fun AddBalancePage(modifier: Modifier = Modifier, navController: NavController, walletViewModel: WalletViewModel) {
    var amount by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Add Balance", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text(text = "Amount to Add") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Validate and add balance
            val amountValue = amount.toDoubleOrNull()
            if (amountValue != null && amountValue > 0) {
                walletViewModel.addFunds("userId", amountValue) // Replace "userId" with the actual user ID
                // Optionally, navigate back or show a success message
                navController.popBackStack() // Return to ProfilePage
            } else {
                // Show an error message (could use Toast or Snackbar)
            }
        }) {
            Text(text = "Add Balance")
        }
    }
}
