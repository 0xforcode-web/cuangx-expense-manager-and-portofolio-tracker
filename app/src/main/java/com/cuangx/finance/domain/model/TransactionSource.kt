package com.cuangx.finance.domain.model

enum class TransactionSource {
    EXPENSE,
    PORTFOLIO,
    DEBT;

    val displayName: String
        get() = when (this) {
            EXPENSE -> "Expense"
            PORTFOLIO -> "Portfolio"
            DEBT -> "Debt"
        }
}
