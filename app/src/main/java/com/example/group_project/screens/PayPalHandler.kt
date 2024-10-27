package com.example.group_project.payment

import android.app.Activity
import android.content.Intent
import com.google.ar.core.Config
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import com.paypal.android.sdk.payments.PayPalService
import java.math.BigDecimal

class PayPalHandler(private val activity: Activity) {

    companion object {
        const val PAYPAL_REQUEST_CODE = 123
    }

    fun startPayPalPayment(amount: Double) {
        val paypalPayment = PayPalPayment(
            BigDecimal(amount.toString()),
            "USD", // Currency code
            "Add Balance",
            PayPalPayment.PAYMENT_INTENT_SALE
        )

        val intent = Intent(activity, PaymentActivity::class.java)
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, paypalPayment)

        activity.startActivityForResult(intent, PAYPAL_REQUEST_CODE)
    }

    fun handlePaymentResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val confirmation =
                    data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                confirmation?.let {
                    // Payment was successful
                    // Here you can update the user's balance in your app
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Payment was canceled by the user
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                // Invalid payment
            }
        }
    }
}
