package com.example.group_project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.group_project.navigation.AppNavigation
import com.stripe.android.PaymentConfiguration

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Stripe
        PaymentConfiguration.init(
            context = this,
            publishableKey = "pk_test_4eC39HqLyjWDarjtT1zdp7dc" // Replace with your actual Stripe key
        )

        val authModel: AuthModel by viewModels()
        setContent {
            AppNavigation(
                authModel = authModel
            )
        }
    }

    // Handle the result of the payment intent confirmation
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Handle Stripe payment result
        if (requestCode == 12345) { // Replace with your actual requestCode for Stripe payment
            if (resultCode == Activity.RESULT_OK) {
                // Payment was successful
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
            } else {
                // Payment failed or was canceled
                Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
