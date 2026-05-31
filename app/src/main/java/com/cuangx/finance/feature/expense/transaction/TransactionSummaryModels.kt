package com.cuangx.finance.feature.expense.transaction

import com.cuangx.finance.domain.model.Transaction

data class TransactionDayGroup(
    val date: Long,
    val income: Double,
    val expense: Double,
    val balance: Double,
    val transactions: List<Transaction>,
)

data class TransactionCalendarCell(
    val date: Long,
    val dayOfMonth: Int,
    val isInCurrentMonth: Boolean,
    val income: Double,
    val expense: Double,
    val balance: Double,
)

data class TransactionMonthSummary(
    val year: Int,
    val month: Int,
    val income: Double,
    val expense: Double,
    val balance: Double,
)

data class TransactionTotalSummary(
    val income: Double,
    val expense: Double,
    val balance: Double,
    val transactionCount: Int,
)
