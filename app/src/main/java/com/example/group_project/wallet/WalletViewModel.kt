package com.example.group_project.wallet

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.math.BigDecimal

class WalletViewModel : ViewModel() {
    private val _balance = MutableStateFlow(BigDecimal.ZERO)
    val balance: StateFlow<BigDecimal> get() = _balance

    fun addFunds(userId: String, amount: Double) {
        _balance.value = _balance.value.add(BigDecimal(amount))
    }
}
