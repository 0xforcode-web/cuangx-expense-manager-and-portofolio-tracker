package com.cuangx.finance.feature.debt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.DebtReceivable
import com.cuangx.finance.domain.model.DebtReceivableType
import com.cuangx.finance.domain.model.DebtStatus
import com.cuangx.finance.domain.repository.DebtReceivableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditDebtUiState(
    val type: DebtReceivableType = DebtReceivableType.DEBT,
    val partyName: String = "",
    val originalAmount: String = "",
    val currency: String = "IDR",
    val interestRate: String = "0",
    val dueDate: Long? = null,
    val note: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false
)

sealed class AddEditDebtEvent {
    data object SaveSuccess : AddEditDebtEvent()
    data class ShowError(val message: String) : AddEditDebtEvent()
}

@HiltViewModel
class AddEditDebtViewModel @Inject constructor(
    private val debtRepository: DebtReceivableRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditDebtUiState())
    val uiState: StateFlow<AddEditDebtUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddEditDebtEvent>()
    val event: SharedFlow<AddEditDebtEvent> = _event.asSharedFlow()

    private var editingDebtId: Long = 0

    fun loadDebt(debtId: Long) {
        viewModelScope.launch {
            val debt = debtRepository.getByIdOnce(debtId) ?: return@launch
            editingDebtId = debt.id
            _uiState.value = _uiState.value.copy(
                type = debt.type,
                partyName = debt.partyName,
                originalAmount = debt.originalAmount.toLong().toString(),
                currency = debt.currency,
                interestRate = debt.interestRate.toString(),
                dueDate = debt.dueDate,
                note = debt.note,
                isEditing = true
            )
        }
    }

    fun updateType(type: DebtReceivableType) { _uiState.value = _uiState.value.copy(type = type) }
    fun updatePartyName(name: String) { _uiState.value = _uiState.value.copy(partyName = name) }
    fun updateOriginalAmount(amount: String) { _uiState.value = _uiState.value.copy(originalAmount = amount) }
    fun updateCurrency(currency: String) { _uiState.value = _uiState.value.copy(currency = currency) }
    fun updateInterestRate(rate: String) { _uiState.value = _uiState.value.copy(interestRate = rate) }
    fun updateDueDate(date: Long?) { _uiState.value = _uiState.value.copy(dueDate = date) }
    fun updateNote(note: String) { _uiState.value = _uiState.value.copy(note = note) }

    fun save() {
        val state = _uiState.value
        val amount = state.originalAmount.toDoubleOrNull()

        if (state.partyName.isBlank()) {
            viewModelScope.launch { _event.emit(AddEditDebtEvent.ShowError("Party name cannot be empty")) }
            return
        }
        if (amount == null || amount <= 0) {
            viewModelScope.launch { _event.emit(AddEditDebtEvent.ShowError("Invalid amount")) }
            return
        }

        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val debt = DebtReceivable(
                    id = if (state.isEditing) editingDebtId else 0,
                    type = state.type,
                    partyName = state.partyName,
                    originalAmount = amount,
                    remainingAmount = if (state.isEditing) {
                        debtRepository.getByIdOnce(editingDebtId)?.remainingAmount ?: amount
                    } else amount,
                    currency = state.currency,
                    interestRate = state.interestRate.toDoubleOrNull() ?: 0.0,
                    dateCreated = System.currentTimeMillis(),
                    dueDate = state.dueDate,
                    status = DebtStatus.ACTIVE,
                    note = state.note
                )

                if (state.isEditing) {
                    debtRepository.update(debt)
                } else {
                    debtRepository.insert(debt)
                }

                _event.emit(AddEditDebtEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiState.value = state.copy(isSaving = false)
                _event.emit(AddEditDebtEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }
}
