package com.cuangx.finance.domain.model

data class RecurringTransaction(
    val id: Long = 0,
    val type: TransactionType,
    val amount: Double,
    val accountId: Long,
    val toAccountId: Long? = null,
    val categoryId: Long? = null,
    val frequency: Frequency,
    val nextDate: Long,
    val isActive: Boolean = true,
    val note: String = ""
)
