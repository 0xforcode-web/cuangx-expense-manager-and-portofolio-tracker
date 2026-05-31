package com.cuangx.finance.domain.model

data class DividendRecord(
    val id: Long = 0,
    val holdingId: Long,
    val amount: Double,
    val accountId: Long? = null,
    val date: Long,
    val createdAt: Long = System.currentTimeMillis()
)
