package com.example.group_project.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.group_project.AuthModel
import com.example.group_project.wallet.WalletViewModel
import com.google.firebase.auth.FirebaseAuth
import com.paypal.android.sdk.payments.*
import java.math.BigDecimal
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authModel: AuthModel,
    walletViewModel: WalletViewModel
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current

    // Get the current balance from the WalletViewModel
    val balance by walletViewModel.balance.collectAsState(initial = BigDecimal.ZERO)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Profile Page", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(16.dp))

        currentUser?.let { user ->
            Text(text = "Email: ${user.email ?: "No Email"}", fontSize = 20.sp)
            Text(text = "UID: ${user.uid}", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the current balance
        Text(text = "Current Balance: $${balance.setScale(2)}", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // PayPal button
        Button(onClick = {
            currentUser?.uid?.let { userId ->
                payWithPayPal(walletViewModel, userId, context)
            }
        }) {
            Text(text = "Add Money to Wallet")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            authModel.signOut()
        }) {
            Text(text = "Sign out")
        }
    }
}

// PayPal Payment Function
private fun payWithPayPal(walletViewModel: WalletViewModel, userId: String, context: Context) {
    val paypalConfig = PayPalConfiguration()
        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
        .clientId("AWV4GLCPZmL5T9YwkZNujubrxujWZiUYqT2TepuTGkzYP-vqOco5ESVGaO_qgxTCr68GGb2jL8_TXP3N") // Replace with your actual client ID

    val payment = PayPalPayment(
        BigDecimal("10.00"),  // Amount to be added
        "USD",                // Currency
        "Adding funds to wallet",
        PayPalPayment.PAYMENT_INTENT_SALE
    )

    val intent = Intent(context, PaymentActivity::class.java).apply {
        putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig)
        putExtra(PaymentActivity.EXTRA_PAYMENT, payment)
    }

    (context as Activity).startActivityForResult(intent, PAYPAL_REQUEST_CODE)
}

private const val PAYPAL_REQUEST_CODE = 1234

// Handle PayPal result
fun handlePayPalResult(requestCode: Int, resultCode: Int, data: Intent?, walletViewModel: WalletViewModel, userId: String) {
    if (requestCode == PAYPAL_REQUEST_CODE) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                // On successful payment, add funds to the user's wallet
                walletViewModel.addFunds(userId, 10.00)
            }
            Activity.RESULT_CANCELED -> {
                // Handle cancellation
            }
            PaymentActivity.RESULT_EXTRAS_INVALID -> {
                // Handle invalid extras
            }
        }
    }
}
