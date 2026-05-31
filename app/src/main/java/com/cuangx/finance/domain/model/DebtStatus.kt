package com.cuangx.finance.domain.model

enum class DebtStatus {
    ACTIVE,
    PAID,
    OVERDUE;

    val displayName: String
        get() = when (this) {
            ACTIVE -> "Active"
            PAID -> "Paid"
            OVERDUE -> "Overdue"
        }
}
