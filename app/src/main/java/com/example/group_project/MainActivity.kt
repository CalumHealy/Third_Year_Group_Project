package com.example.group_project

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.group_project.navigation.AppNavigation
import com.paypal.android.sdk.payments.PayPalConfiguration

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authModel: AuthModel by viewModels()
        setContent {
            AppNavigation(
                authModel = authModel
            )
        }
    }}

