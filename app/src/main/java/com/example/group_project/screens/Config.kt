package com.example.group_project.screens


import com.paypal.android.sdk.payments.PayPalConfiguration

object Config {
    const val CLIENT_ID = "AWV4GLCPZmL5T9YwkZNujubrxujWZiUYqT2TepuTGkzYP-vqOco5ESVGaO_qgxTCr68GGb2jL8_TXP3N" // Replace with your PayPal client ID

    val PAYPAL_CONFIG = PayPalConfiguration()
        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
        .clientId(CLIENT_ID)

    const val ENVIRONMENT = "sandbox"
}





