package com.example.group_project

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.group_project.screens.HomePage
import com.example.group_project.wallet.WalletViewModel
import com.paypal.android.sdk.payments.PayPalConfiguration

class MainActivity : ComponentActivity() {
    private lateinit var payPalConfig: PayPalConfiguration // Declare the PayPalConfiguration
    private val walletViewModel: WalletViewModel by viewModels() // Initialize WalletViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create an instance of AuthModel
        val authModel: AuthModel by viewModels()

        // Initialize the PayPal configuration
        payPalConfig = createPayPalConfig()

        setContent {
            // Create NavController instance
            val navController = rememberNavController()

            // Pass both navController and authModel to HomePage
            HomePage(navController = navController, authModel = authModel)
        }
    }

    // Method to create PayPal configuration
    private fun createPayPalConfig() = PayPalConfiguration()
        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
        .clientId("AWV4GLCPZmL5T9YwkZNujubrxujWZiUYqT2TepuTGkzYP-vqOco5ESVGaO_qgxTCr68GGb2jL8_TXP3N")

    }


