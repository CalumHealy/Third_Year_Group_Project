package com.example.group_project.screens

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.view.CardInputWidget
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AddFundsPage(onPaymentSuccess: () -> Unit) {
    val context = LocalContext.current
    var amount by remember { mutableStateOf("") } // State for the amount entered by the user
    var isProcessing by remember { mutableStateOf(false) }

    // Initialize Stripe with your Publishable Key
    PaymentConfiguration.init(
        context = context,
        publishableKey = "pk_test_51QP33kHTr0XTBmKNb2KYDMsYdKL3OVEICVygQ7ohjGX0s0sqK3iNtDUJP5hdAfaZSStRBooLblvdbdq4Hq3fUo4J00FAx5jD9n" // Replace with your actual Stripe Publishable Key
    )

    val stripe = Stripe(context, PaymentConfiguration.getInstance(context).publishableKey)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Add Funds to Wallet",
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input field for the amount
        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Enter Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Card Input Field for User to enter their payment info
        var cardInputWidget by remember { mutableStateOf<CardInputWidget?>(null) }

        AndroidView(
            factory = { context ->
                CardInputWidget(context).apply {
                    cardInputWidget = this
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (amount.isNotEmpty() && amount.toDouble() > 0) {
                    isProcessing = true

                    // Extract card details from CardInputWidget
                    val cardParams = cardInputWidget?.paymentMethodCreateParams
                    if (cardParams != null) {
                        // Here, directly confirm the payment intent (without backend)
                        // In this case, let's assume we use a static test payment intent
                        val confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(
                            cardParams,
                            "client_secret_from_stripe" // This is a placeholder; replace with actual client secret
                        )

                        stripe.confirmPayment(
                            context as ComponentActivity,
                            confirmParams
                        )
                    } else {
                        Toast.makeText(context, "Invalid card details", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Pay Now")
        }

        if (isProcessing) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Processing Payment...", fontSize = 18.sp)
        }
    }
}
