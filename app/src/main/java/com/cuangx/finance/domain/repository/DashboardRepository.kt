package com.cuangx.finance.domain.repository

import com.cuangx.finance.domain.model.DebtReceivable
import com.cuangx.finance.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getTotalNetWorth(): Flow<Double>
    fun getMonthlyIncome(): Flow<Double>
    fun getMonthlyExpense(): Flow<Double>
    fun getRecentTransactions(limit: Int): Flow<List<Transaction>>
    fun getUpcomingDueDebts(now: Long, futureDate: Long): Flow<List<DebtReceivable>>
}
