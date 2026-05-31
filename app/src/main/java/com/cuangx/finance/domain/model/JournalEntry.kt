package com.cuangx.finance.domain.model

data class JournalEntry(
    val id: Long = 0,
    val action: JournalAction,
    val assetType: AssetType,
    val ticker: String? = null,
    val name: String,
    val quantity: Double,
    val price: Double,
    val fee: Double = 0.0,
    val accountId: Long,
    val transactionId: Long? = null,
    val reason: String = "",
    val tags: String = "",
    val date: Long,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalAmount: Double
        get() = (quantity * price) + fee

    val isBuy: Boolean
        get() = action == JournalAction.BUY

    val isSell: Boolean
        get() = action == JournalAction.SELL

    val isDividend: Boolean
        get() = action == JournalAction.DIVIDEND
}
