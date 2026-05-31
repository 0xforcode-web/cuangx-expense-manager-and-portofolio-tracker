package com.cuangx.finance.domain.model

enum class Frequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY;

    val displayName: String
        get() = when (this) {
            DAILY -> "Daily"
            WEEKLY -> "Weekly"
            MONTHLY -> "Monthly"
            YEARLY -> "Yearly"
        }
}
