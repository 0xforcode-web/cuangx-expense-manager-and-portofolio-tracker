package com.cuangx.finance.domain.model

enum class JournalAction {
    BUY,
    SELL,
    DIVIDEND;

    val displayName: String
        get() = when (this) {
            BUY -> "Beli"
            SELL -> "Jual"
            DIVIDEND -> "Dividen"
        }
}
