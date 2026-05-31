package com.cuangx.finance.data.repository

import com.cuangx.finance.domain.model.DebtReceivable
import com.cuangx.finance.domain.model.DebtReceivableType
import com.cuangx.finance.domain.model.JournalAction
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.repository.AccountRepository
import com.cuangx.finance.domain.repository.DashboardRepository
import com.cuangx.finance.domain.repository.DebtReceivableRepository
import com.cuangx.finance.domain.repository.JournalRepository
import com.cuangx.finance.domain.repository.PriceRepository
import com.cuangx.finance.domain.repository.TransactionRepository
import com.cuangx.finance.core.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val accountRepository: AccountRepository,
    private val journalRepository: JournalRepository,
    private val priceRepository: PriceRepository,
    private val transactionRepository: TransactionRepository,
    private val debtReceivableRepository: DebtReceivableRepository
) : DashboardRepository {

    override fun getTotalNetWorth(): Flow<Double> {
        return combine(
            accountRepository.getTotalBalance(),
            journalRepository.getAll(),
            priceRepository.getAllCached(),
            debtReceivableRepository.getTotalRemainingByType(DebtReceivableType.RECEIVABLE),
            debtReceivableRepository.getTotalRemainingByType(DebtReceivableType.DEBT)
        ) { accounts, journalEntries, prices, receivable, debt ->
            val priceMap = prices.associateBy { it.ticker }

            val portfolioValue = journalEntries
                .filter { it.action == JournalAction.BUY || it.action == JournalAction.SELL }
                .groupBy { it.ticker ?: it.name }
                .mapNotNull { (ticker, entries) ->
                    val buyQty = entries.filter { it.action == JournalAction.BUY }.sumOf { it.quantity }
                    val sellQty = entries.filter { it.action == JournalAction.SELL }.sumOf { it.quantity }
                    val remaining = buyQty - sellQty
                    if (remaining <= 0) return@mapNotNull null

                    val buyCost = entries.filter { it.action == JournalAction.BUY }.sumOf { it.quantity * it.price }
                    val avgPrice = if (buyQty > 0) buyCost / buyQty else 0.0
                    val currentPrice = priceMap[ticker]?.price ?: avgPrice
                    remaining * currentPrice
                }
                .sum()

            accounts + portfolioValue + receivable - debt
        }
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
