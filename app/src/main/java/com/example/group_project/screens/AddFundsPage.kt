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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AddFundsPage(onPaymentSuccess: () -> Unit) {
    val context = LocalContext.current
    var amount by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Initialize Stripe with publishable Key
    PaymentConfiguration.init(
        context = context,
        publishableKey = "pk_test_51QP33kHTr0XTBmKNb2KYDMsYdKL3OVEICVygQ7ohjGX0s0sqK3iNtDUJP5hdAfaZSStRBooLblvdbdq4Hq3fUo4J00FAx5jD9n"
    )

    val stripe = Stripe(context, PaymentConfiguration.getInstance(context).publishableKey)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Add Funds to Wallet", fontSize = 22.sp)

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

                        val confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(
                            cardParams,
                            "sk_test_51QP33kHTr0XTBmKNyn7qM8UWT2qdZEfmyVyJzx10hVpnVqa4XjWiY27jawwwm7uiOyeLfU6paWwFFXlSz7y6dgNL000IajvXO9"
                        )

                        stripe.confirmPayment(
                            context as ComponentActivity,
                            confirmParams
                        )

                        // Update balance in Firestore
                        currentUser?.let { user ->
                            val userRef = firestore.collection("users").document(user.uid)
                            userRef.get().addOnSuccessListener { document ->
                                val currentBalance = document.getDouble("balance") ?: 0.0
                                val newBalance = currentBalance + amount.toDouble()
                                userRef.update("balance", newBalance).addOnSuccessListener {
                                    Toast.makeText(context, "Funds added successfully!", Toast.LENGTH_SHORT).show()
                                    isProcessing = false
                                    onPaymentSuccess()
                                }.addOnFailureListener {
                                    Toast.makeText(context, "Failed to update balance", Toast.LENGTH_SHORT).show()
                                    isProcessing = false
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Invalid card details", Toast.LENGTH_SHORT).show()
                        isProcessing = false
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
