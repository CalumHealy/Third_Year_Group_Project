package com.example.group_project

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.group_project.navigation.AppNavigation
import com.example.group_project.payment.PayPalHandler
import com.example.group_project.screens.HomePage

import com.example.group_project.wallet.WalletViewModel
import com.paypal.android.sdk.payments.PayPalConfiguration

class MainActivity : ComponentActivity() {
    private lateinit var payPalConfig: PayPalConfiguration
    private val walletViewModel: WalletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authModel: AuthModel by viewModels() // Initialize AuthModel

        payPalConfig = createPayPalConfig()

        setContent {
            AppNavigation(
                authModel = authModel, // Pass authModel here
                walletViewModel = walletViewModel // Pass walletViewModel here
            )
        }
    }

    private fun createPayPalConfig() = PayPalConfiguration()
        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
        .clientId("AWV4GLCPZmL5T9YwkZNujubrxujWZiUYqT2TepuTGkzYP-vqOco5ESVGaO_qgxTCr68GGb2jL8_TXP3N")

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Call the PayPal handler to process the payment result
        PayPalHandler(this).handlePaymentResult(requestCode, resultCode, data)
    }
}
