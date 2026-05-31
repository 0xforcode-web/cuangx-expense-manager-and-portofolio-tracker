package com.cuangx.finance.domain.usecase

import com.cuangx.finance.domain.model.DebtReceivableType
import com.cuangx.finance.domain.repository.AccountRepository
import com.cuangx.finance.domain.repository.DebtReceivableRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class NetWorthSummary(
    val totalNetWorth: Double,
    val liquidAssets: Double, // Accounts
    val investments: Double, // Portfolio
    val receivables: Double,
    val debts: Double
)

class NetWorthUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val portfolioUseCase: PortfolioUseCase,
    private val debtReceivableRepository: DebtReceivableRepository
) {
    fun getNetWorthSummary(): Flow<NetWorthSummary> {
        return combine(
            accountRepository.getTotalBalance(),
            portfolioUseCase.getPortfolioSummary(),
            debtReceivableRepository.getTotalRemainingByType(DebtReceivableType.RECEIVABLE),
            debtReceivableRepository.getTotalRemainingByType(DebtReceivableType.DEBT)
        ) { accounts, portfolio, receivable, debt ->
            val liquidAssets = accounts ?: 0.0
            val investments = portfolio.totalValue
            val netWorth = liquidAssets + investments + receivable - debt

            NetWorthSummary(
                totalNetWorth = netWorth,
                liquidAssets = liquidAssets,
                investments = investments,
                receivables = receivable,
                debts = debt
            )
        }
    }
}
