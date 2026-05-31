package com.cuangx.finance.domain.model

data class Transaction(
    val id: Long = 0,
    val type: TransactionType,
    val amount: Double,
    val accountId: Long,
    val toAccountId: Long? = null,
    val categoryId: Long? = null,
    val date: Long,
    val note: String = "",
    val photoUri: String? = null,
    val isBookmarked: Boolean = false,
    val linkedHoldingId: Long? = null,
    val linkedDividendId: Long? = null,
    val linkedDebtId: Long? = null,
    val linkedReceivableId: Long? = null,
    val source: TransactionSource = TransactionSource.EXPENSE,
    val createdAt: Long = System.currentTimeMillis()
)
