package com.cuangx.finance.feature.portfolio.holding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.JournalAction
import com.cuangx.finance.domain.model.JournalEntry
import com.cuangx.finance.domain.model.PriceData
import com.cuangx.finance.domain.repository.JournalRepository
import com.cuangx.finance.domain.repository.PriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HoldingDetailUiState(
    val entries: List<JournalEntry> = emptyList(),
    val priceData: PriceData? = null,
    val currentQty: Double = 0.0,
    val avgBuyPrice: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val currentPrice: Double get() = priceData?.price ?: avgBuyPrice
    val currentValue: Double get() = currentQty * currentPrice
    val totalCost: Double get() = currentQty * avgBuyPrice
    val pnl: Double get() = currentValue - totalCost
    val pnlPercent: Double get() = if (totalCost > 0) (pnl / totalCost) * 100 else 0.0
}

@HiltViewModel
class HoldingDetailViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val priceRepository: PriceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val ticker: String = savedStateHandle.get<String>("ticker") ?: ""

    private val _uiState = MutableStateFlow(HoldingDetailUiState())
    val uiState: StateFlow<HoldingDetailUiState> = _uiState.asStateFlow()

    init {
        loadHoldingData()
    }

    private fun loadHoldingData() {
        viewModelScope.launch {
            try {
                val entries = journalRepository.getByTickerOnce(ticker)
                val priceData = priceRepository.getPrice(ticker)
                val currentQty = journalRepository.getCurrentQuantity(ticker)

                val buys = entries.filter { it.action == JournalAction.BUY }
                val totalBuyQty = buys.sumOf { it.quantity }
                val totalBuyCost = buys.sumOf { it.quantity * it.price }
                val avgBuyPrice = if (totalBuyQty > 0) totalBuyCost / totalBuyQty else 0.0

                _uiState.value = HoldingDetailUiState(
                    entries = entries,
                    priceData = priceData,
                    currentQty = currentQty,
                    avgBuyPrice = avgBuyPrice,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load holding data"
                )
            }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadHoldingData()
    }
}
