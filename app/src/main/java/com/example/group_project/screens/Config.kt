package com.example.group_project.screens


import com.paypal.android.sdk.payments.PayPalConfiguration

object Config {
    // Set up your PayPal client ID
    const val CLIENT_ID = "AWV4GLCPZmL5T9YwkZNujubrxujWZiUYqT2TepuTGkzYP-vqOco5ESVGaO_qgxTCr68GGb2jL8_TXP3N" // Replace with your PayPal client ID

    val PAYPAL_CONFIG = PayPalConfiguration()
        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) // Use ENVIRONMENT_PRODUCTION for live
        .clientId(CLIENT_ID)
}
