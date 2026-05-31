package com.cuangx.finance.data.repository

import com.cuangx.finance.core.util.DateUtils
import com.cuangx.finance.domain.model.DebtReceivable
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.repository.DashboardRepository
import com.cuangx.finance.domain.repository.DebtReceivableRepository
import com.cuangx.finance.domain.repository.TransactionRepository
import com.cuangx.finance.domain.usecase.NetWorthUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val debtReceivableRepository: DebtReceivableRepository,
    private val netWorthUseCase: NetWorthUseCase
) : DashboardRepository {

    override fun getTotalNetWorth(): Flow<Double> {
        return netWorthUseCase.getNetWorthSummary().map { it.totalNetWorth }
    }

    override fun getMonthlyIncome(): Flow<Double> {
        val startOfMonth = DateUtils.getStartOfMonth(System.currentTimeMillis())
        val endOfMonth = DateUtils.getEndOfMonth(System.currentTimeMillis())
        return transactionRepository.getTotalIncomeByDateRange(startOfMonth, endOfMonth)
    }

    override fun getMonthlyExpense(): Flow<Double> {
        val startOfMonth = DateUtils.getStartOfMonth(System.currentTimeMillis())
        val endOfMonth = DateUtils.getEndOfMonth(System.currentTimeMillis())
        return transactionRepository.getTotalExpenseByDateRange(startOfMonth, endOfMonth)
    }

    override fun getRecentTransactions(limit: Int): Flow<List<Transaction>> {
        return transactionRepository.getRecent(limit)
    }

    override fun getUpcomingDueDebts(now: Long, futureDate: Long): Flow<List<DebtReceivable>> {
        return debtReceivableRepository.getUpcomingDue(now, futureDate)
    }
}
