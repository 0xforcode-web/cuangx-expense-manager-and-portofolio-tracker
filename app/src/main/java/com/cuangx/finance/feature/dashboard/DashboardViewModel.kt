package com.cuangx.finance.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.DebtReceivable
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.repository.DashboardRepository
import com.cuangx.finance.core.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class DashboardUiState(
    val netWorth: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val upcomingDebts: List<DebtReceivable> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val now = System.currentTimeMillis()
    private val nextWeek = now + 7 * 24 * 60 * 60 * 1000

    val uiState: StateFlow<DashboardUiState> = combine(
        dashboardRepository.getTotalNetWorth(),
        dashboardRepository.getMonthlyIncome(),
        dashboardRepository.getMonthlyExpense(),
        dashboardRepository.getRecentTransactions(10),
        dashboardRepository.getUpcomingDueDebts(now, nextWeek)
    ) { netWorth, income, expense, transactions, upcomingDebts ->
        DashboardUiState(
            netWorth = netWorth,
            monthlyIncome = income,
            monthlyExpense = expense,
            recentTransactions = transactions,
            upcomingDebts = upcomingDebts,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )
}
