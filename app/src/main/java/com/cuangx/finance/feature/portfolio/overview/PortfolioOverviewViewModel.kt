package com.cuangx.finance.feature.portfolio.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.repository.JournalRepository
import com.cuangx.finance.domain.repository.PriceRepository
import com.cuangx.finance.domain.usecase.HoldingPosition
import com.cuangx.finance.domain.usecase.PortfolioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PortfolioUiState(
    val holdings: List<HoldingPosition> = emptyList(),
    val totalValue: Double = 0.0,
    val totalCost: Double = 0.0,
    val totalPnl: Double = 0.0,
    val totalPnlPercent: Double = 0.0,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class PortfolioOverviewViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val priceRepository: PriceRepository,
    private val portfolioUseCase: PortfolioUseCase
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<PortfolioUiState> = combine(
        portfolioUseCase.getPortfolioSummary(),
        _isRefreshing
    ) { summary, isRefreshing ->
        PortfolioUiState(
            holdings = summary.holdings,
            totalValue = summary.totalValue,
            totalCost = summary.totalCost,
            totalPnl = summary.totalPnl,
            totalPnlPercent = summary.totalPnlPercent,
            isLoading = false,
            isRefreshing = isRefreshing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PortfolioUiState()
    )

    fun refreshPrices() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val tickers = journalRepository.getAllTickers()
            if (tickers.isNotEmpty()) {
                priceRepository.refreshPrices(tickers)
            }
            priceRepository.refreshGoldPrice()
            _isRefreshing.value = false
        }
    }
}
