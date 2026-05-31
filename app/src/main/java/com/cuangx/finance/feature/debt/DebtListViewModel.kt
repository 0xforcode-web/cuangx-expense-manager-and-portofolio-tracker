package com.cuangx.finance.feature.debt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.DebtReceivable
import com.cuangx.finance.domain.model.DebtReceivableType
import com.cuangx.finance.domain.repository.DebtReceivableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DebtListUiState(
    val debts: List<DebtReceivable> = emptyList(),
    val receivables: List<DebtReceivable> = emptyList(),
    val totalDebt: Double = 0.0,
    val totalReceivable: Double = 0.0,
    val isLoading: Boolean = true
)

@HiltViewModel
class DebtListViewModel @Inject constructor(
    private val debtRepository: DebtReceivableRepository
) : ViewModel() {

    val uiState: StateFlow<DebtListUiState> = combine(
        debtRepository.getByType(DebtReceivableType.DEBT),
        debtRepository.getByType(DebtReceivableType.RECEIVABLE),
        debtRepository.getTotalRemainingByType(DebtReceivableType.DEBT),
        debtRepository.getTotalRemainingByType(DebtReceivableType.RECEIVABLE)
    ) { debts, receivables, totalDebt, totalReceivable ->
        DebtListUiState(
            debts = debts,
            receivables = receivables,
            totalDebt = totalDebt,
            totalReceivable = totalReceivable,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DebtListUiState()
    )

    fun deleteDebt(debt: DebtReceivable) {
        viewModelScope.launch {
            debtRepository.delete(debt)
        }
    }
}
