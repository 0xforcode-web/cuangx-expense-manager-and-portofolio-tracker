package com.cuangx.finance.domain.model

enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER;

    val displayName: String
        get() = when (this) {
            INCOME -> "Income"
            EXPENSE -> "Expense"
            TRANSFER -> "Transfer"
        }
}
