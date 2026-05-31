package com.cuangx.finance.domain.model

data class Budget(
    val id: Long = 0,
    val categoryId: Long,
    val amount: Double,
    val period: BudgetPeriod,
    val startDate: Long,
    val isActive: Boolean = true
)
