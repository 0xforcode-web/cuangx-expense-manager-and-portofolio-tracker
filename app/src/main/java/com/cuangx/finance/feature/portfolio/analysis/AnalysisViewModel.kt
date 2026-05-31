package com.cuangx.finance.feature.portfolio.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.usecase.HoldingPosition
import com.cuangx.finance.domain.usecase.PortfolioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AssetAllocation(
    val assetType: String,
    val value: Double,
    val percentage: Double,
    val count: Int
)

data class AnalysisUiState(
    val allocations: List<AssetAllocation> = emptyList(),
    val totalValue: Double = 0.0,
    val isLoading: Boolean = true
)

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val portfolioUseCase: PortfolioUseCase
) : ViewModel() {

    val uiState: StateFlow<AnalysisUiState> = portfolioUseCase.getPortfolioSummary()
        .map { summary ->
            val totalValue = summary.totalValue
            val allocations = summary.holdings.groupBy { it.assetType }
                .map { (type, holdings) ->
                    val value = holdings.sumOf { it.currentValue }
                    AssetAllocation(
                        assetType = type.displayName,
                        value = value,
                        percentage = if (totalValue > 0) (value / totalValue) * 100 else 0.0,
                        count = holdings.size
                    )
                }
                .sortedByDescending { it.value }

            AnalysisUiState(
                allocations = allocations,
                totalValue = totalValue,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AnalysisUiState()
        )
}
