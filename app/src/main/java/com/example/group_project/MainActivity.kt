package com.example.group_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController // Import for NavController
import com.example.group_project.navigation.AppNavigation
import com.example.group_project.screens.HomePage
import com.example.group_project.wallet.WalletViewModel
import com.paypal.android.sdk.payments.PayPalConfiguration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authModel: AuthModel by viewModels()
        val walletViewModel: WalletViewModel by viewModels()

        setContent {
            AppNavigation(authModel = authModel, walletViewModel = walletViewModel)
        }
    }
}
