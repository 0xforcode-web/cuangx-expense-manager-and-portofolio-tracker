package com.cuangx.finance.feature.portfolio.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.AssetType
import com.cuangx.finance.domain.model.Holding
import com.cuangx.finance.domain.model.PriceData
import com.cuangx.finance.domain.repository.HoldingRepository
import com.cuangx.finance.domain.repository.PriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AssetAllocation(
    val type: AssetType,
    val value: Double,
    val percentage: Double
)

data class AnalysisUiState(
    val allocations: List<AssetAllocation> = emptyList(),
    val totalValue: Double = 0.0,
    val isLoading: Boolean = true
)

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    holdingRepository: HoldingRepository,
    priceRepository: PriceRepository
) : ViewModel() {

    val uiState: StateFlow<AnalysisUiState> = combine(
        holdingRepository.getAll(),
        priceRepository.getAllCached()
    ) { holdings, prices ->
        val priceMap = prices.associateBy { it.ticker }
        val totalValue = holdings.sumOf { holding ->
            val price = holding.ticker?.let { priceMap[it]?.price } ?: holding.avgBuyPrice
            holding.quantity * price
        }

        val allocations = AssetType.entries.map { type ->
            val typeHoldings = holdings.filter { it.assetType == type }
            val typeValue = typeHoldings.sumOf { holding ->
                val price = holding.ticker?.let { priceMap[it]?.price } ?: holding.avgBuyPrice
                holding.quantity * price
            }
            AssetAllocation(
                type = type,
                value = typeValue,
                percentage = if (totalValue > 0) (typeValue / totalValue) * 100 else 0.0
            )
        }.filter { it.value > 0 }.sortedByDescending { it.value }

        AnalysisUiState(
            allocations = allocations,
            totalValue = totalValue,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalysisUiState()
    )
}
