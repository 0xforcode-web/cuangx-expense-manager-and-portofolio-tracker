package com.cuangx.finance.feature.portfolio.holding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.JournalAction
import com.cuangx.finance.domain.model.JournalEntry
import com.cuangx.finance.domain.model.PriceData
import com.cuangx.finance.domain.repository.JournalRepository
import com.cuangx.finance.domain.repository.PriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HoldingDetailUiState(
    val ticker: String = "",
    val name: String = "",
    val assetType: String = "",
    val entries: List<JournalEntry> = emptyList(),
    val priceData: PriceData? = null,
    val currentQty: Double = 0.0,
    val avgBuyPrice: Double = 0.0,
    val currentValue: Double = 0.0,
    val totalCost: Double = 0.0,
    val pnl: Double = 0.0,
    val pnlPercent: Double = 0.0,
    val error: String? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class HoldingDetailViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val priceRepository: PriceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HoldingDetailUiState())
    val uiState: StateFlow<HoldingDetailUiState> = _uiState.asStateFlow()

    private var currentJob: kotlinx.coroutines.Job? = null

    fun loadHolding(ticker: String) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, ticker = ticker)
            
            val entriesFlow = journalRepository.getByTicker(ticker)
            val priceFlow = priceRepository.observePrice(ticker)
            val qtyFlow = flow { emit(journalRepository.getCurrentQuantity(ticker)) }

            combine(entriesFlow, priceFlow, qtyFlow) { entries, price, qty ->
                if (entries.isEmpty()) {
                    return@combine _uiState.value.copy(isLoading = false, error = "Aset tidak ditemukan")
                }

                val firstEntry = entries.first()
                val buys = entries.filter { it.action == JournalAction.BUY }
                val totalBuyQty = buys.sumOf { it.quantity }
                val totalBuyCost = buys.sumOf { it.quantity * it.price + it.fee }
                val avgBuyPrice = if (totalBuyQty > 0) totalBuyCost / totalBuyQty else 0.0
                
                val goldPriceData = if (firstEntry.assetType == com.cuangx.finance.domain.model.AssetType.GOLD) {
                    priceRepository.getPrice("GOLD_GRAM_IDR")
                } else null

                val currentPrice = when (firstEntry.assetType) {
                    com.cuangx.finance.domain.model.AssetType.GOLD -> goldPriceData?.price ?: avgBuyPrice
                    else -> price?.price ?: avgBuyPrice
                }
                
                val currentValue = qty * currentPrice
                val totalCost = qty * avgBuyPrice
                val pnl = currentValue - totalCost
                val pnlPercent = if (totalCost > 0) (pnl / totalCost) * 100 else 0.0

                HoldingDetailUiState(
                    ticker = ticker,
                    name = firstEntry.name,
                    assetType = firstEntry.assetType.displayName,
                    entries = entries.sortedByDescending { it.date },
                    priceData = price ?: goldPriceData,
                    currentQty = qty,
                    avgBuyPrice = avgBuyPrice,
                    currentValue = currentValue,
                    totalCost = totalCost,
                    pnl = pnl,
                    pnlPercent = pnlPercent,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
}
