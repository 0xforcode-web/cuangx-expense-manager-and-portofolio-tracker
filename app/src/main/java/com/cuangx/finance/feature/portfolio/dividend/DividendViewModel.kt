package com.cuangx.finance.feature.portfolio.dividend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.DividendRecord
import com.cuangx.finance.domain.model.Holding
import com.cuangx.finance.domain.repository.DividendRepository
import com.cuangx.finance.domain.repository.HoldingRepository
import com.cuangx.finance.core.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DividendWithHolding(
    val record: DividendRecord,
    val holdingName: String
)

data class DividendUiState(
    val dividends: List<DividendWithHolding> = emptyList(),
    val totalThisYear: Double = 0.0,
    val isLoading: Boolean = true
)

@HiltViewModel
class DividendViewModel @Inject constructor(
    private val dividendRepository: DividendRepository,
    private val holdingRepository: HoldingRepository
) : ViewModel() {

    private val now = System.currentTimeMillis()
    private val startOfYear = DateUtils.getStartOfYear(now)
    private val endOfYear = DateUtils.getEndOfYear(now)

    val uiState: StateFlow<DividendUiState> = combine(
        dividendRepository.getAll(),
        dividendRepository.getTotalByDateRange(startOfYear, endOfYear),
        holdingRepository.getAll()
    ) { dividends, totalThisYear, holdings ->
        val holdingMap = holdings.associateBy { it.id }
        val dividendsWithHolding = dividends.map { record ->
            DividendWithHolding(
                record = record,
                holdingName = holdingMap[record.holdingId]?.name ?: "Unknown"
            )
        }
        DividendUiState(
            dividends = dividendsWithHolding,
            totalThisYear = totalThisYear,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DividendUiState()
    )

    fun deleteDividend(record: DividendRecord) {
        viewModelScope.launch {
            dividendRepository.delete(record)
        }
    }
}
