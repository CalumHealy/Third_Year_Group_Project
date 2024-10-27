package com.example.group_project.wallet

data class Wallet(
    val userId: String,
    var balance: Double = 0.0,
    val transactions: MutableList<Transaction> = mutableListOf()
)

data class Transaction(
    val amount: Double,
    val timestamp: Long,
    val description: String
)
