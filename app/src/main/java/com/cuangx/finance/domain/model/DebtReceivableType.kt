package com.cuangx.finance.domain.model

enum class DebtReceivableType {
    DEBT,
    RECEIVABLE;

    val displayName: String
        get() = when (this) {
            DEBT -> "Utang"
            RECEIVABLE -> "Piutang"
        }
}
