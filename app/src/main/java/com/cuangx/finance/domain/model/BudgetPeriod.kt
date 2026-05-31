package com.cuangx.finance.domain.model

enum class BudgetPeriod {
    WEEKLY,
    MONTHLY,
    YEARLY;

    val displayName: String
        get() = when (this) {
            WEEKLY -> "Weekly"
            MONTHLY -> "Monthly"
            YEARLY -> "Yearly"
        }
}
