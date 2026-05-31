package com.cuangx.finance.feature.expense.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.RecurringTransaction
import com.cuangx.finance.domain.repository.RecurringRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecurringUiState(
    val recurringList: List<RecurringTransaction> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class RecurringViewModel @Inject constructor(
    private val recurringRepository: RecurringRepository
) : ViewModel() {

    val uiState: StateFlow<RecurringUiState> = recurringRepository.getAll()
        .map { recurringList ->
            RecurringUiState(recurringList = recurringList, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RecurringUiState()
        )

    fun toggleActive(recurring: RecurringTransaction) {
        viewModelScope.launch {
            recurringRepository.updateActiveStatus(recurring.id, !recurring.isActive)
        }
    }

    fun deleteRecurring(recurring: RecurringTransaction) {
        viewModelScope.launch {
            recurringRepository.delete(recurring)
        }
    }
}
