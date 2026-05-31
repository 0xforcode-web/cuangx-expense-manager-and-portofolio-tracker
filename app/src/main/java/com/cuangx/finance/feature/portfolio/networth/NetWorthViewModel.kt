package com.cuangx.finance.feature.portfolio.networth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.DebtReceivableType
import com.cuangx.finance.domain.model.JournalAction
import com.cuangx.finance.domain.repository.AccountRepository
import com.cuangx.finance.domain.repository.DebtReceivableRepository
import com.cuangx.finance.domain.repository.JournalRepository
import com.cuangx.finance.domain.repository.PriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class NetWorthUiState(
    val totalAccounts: Double = 0.0,
    val totalPortfolio: Double = 0.0,
    val totalReceivable: Double = 0.0,
    val totalDebt: Double = 0.0,
    val netWorth: Double = 0.0,
    val isLoading: Boolean = true
)

@HiltViewModel
class NetWorthViewModel @Inject constructor(
    accountRepository: AccountRepository,
    journalRepository: JournalRepository,
    priceRepository: PriceRepository,
    debtReceivableRepository: DebtReceivableRepository
) : ViewModel() {

    val uiState: StateFlow<NetWorthUiState> = combine(
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

        val netWorth = accounts + portfolioValue + receivable - debt

        NetWorthUiState(
            totalAccounts = accounts,
            totalPortfolio = portfolioValue,
            totalReceivable = receivable,
            totalDebt = debt,
            netWorth = netWorth,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NetWorthUiState()
    )
}
