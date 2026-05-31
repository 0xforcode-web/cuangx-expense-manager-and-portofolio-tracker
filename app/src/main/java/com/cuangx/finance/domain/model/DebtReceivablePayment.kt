package com.cuangx.finance.domain.model

data class DebtReceivablePayment(
    val id: Long = 0,
    val debtId: Long,
    val amount: Double,
    val accountId: Long,
    val transactionId: Long? = null,
    val date: Long,
    val createdAt: Long = System.currentTimeMillis()
)
