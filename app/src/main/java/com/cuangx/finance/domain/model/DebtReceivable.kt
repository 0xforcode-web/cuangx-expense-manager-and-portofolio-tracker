package com.cuangx.finance.domain.model

data class DebtReceivable(
    val id: Long = 0,
    val type: DebtReceivableType,
    val partyName: String,
    val originalAmount: Double,
    val remainingAmount: Double,
    val currency: String = "IDR",
    val interestRate: Double = 0.0,
    val dateCreated: Long,
    val dueDate: Long? = null,
    val status: DebtStatus = DebtStatus.ACTIVE,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
