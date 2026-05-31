package com.cuangx.finance.feature.expense.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Account
import com.cuangx.finance.domain.model.Category
import com.cuangx.finance.domain.model.Frequency
import com.cuangx.finance.domain.model.RecurringTransaction
import com.cuangx.finance.domain.model.TransactionType
import com.cuangx.finance.domain.repository.AccountRepository
import com.cuangx.finance.domain.repository.CategoryRepository
import com.cuangx.finance.domain.repository.RecurringRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditRecurringUiState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val accountId: Long? = null,
    val toAccountId: Long? = null,
    val categoryId: Long? = null,
    val frequency: Frequency = Frequency.MONTHLY,
    val nextDate: Long = System.currentTimeMillis(),
    val note: String = "",
    val isActive: Boolean = true,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList()
)

sealed class AddEditRecurringEvent {
    data object SaveSuccess : AddEditRecurringEvent()
    data class ShowError(val message: String) : AddEditRecurringEvent()
}

@HiltViewModel
class AddEditRecurringViewModel @Inject constructor(
    private val recurringRepository: RecurringRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditRecurringUiState())
    val uiState: StateFlow<AddEditRecurringUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddEditRecurringEvent>()
    val event: SharedFlow<AddEditRecurringEvent> = _event.asSharedFlow()

    private var editingRecurringId: Long = 0

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            accountRepository.getAllActive().collect { accounts ->
                _uiState.value = _uiState.value.copy(accounts = accounts)
            }
        }
        viewModelScope.launch {
            categoryRepository.getAll().collect { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
            }
        }
    }

    fun loadRecurring(recurringId: Long) {
        viewModelScope.launch {
            val recurring = recurringRepository.getByIdOnce(recurringId) ?: return@launch
            editingRecurringId = recurring.id
            _uiState.value = _uiState.value.copy(
                type = recurring.type,
                amount = recurring.amount.toLong().toString(),
                accountId = recurring.accountId,
                toAccountId = recurring.toAccountId,
                categoryId = recurring.categoryId,
                frequency = recurring.frequency,
                nextDate = recurring.nextDate,
                note = recurring.note,
                isActive = recurring.isActive,
                isEditing = true
            )
        }
    }

    fun updateType(type: TransactionType) { _uiState.value = _uiState.value.copy(type = type) }
    fun updateAmount(amount: String) { _uiState.value = _uiState.value.copy(amount = amount) }
    fun updateAccountId(id: Long) { _uiState.value = _uiState.value.copy(accountId = id) }
    fun updateToAccountId(id: Long) { _uiState.value = _uiState.value.copy(toAccountId = id) }
    fun updateCategoryId(id: Long) { _uiState.value = _uiState.value.copy(categoryId = id) }
    fun updateFrequency(freq: Frequency) { _uiState.value = _uiState.value.copy(frequency = freq) }
    fun updateNextDate(date: Long) { _uiState.value = _uiState.value.copy(nextDate = date) }
    fun updateNote(note: String) { _uiState.value = _uiState.value.copy(note = note) }

    fun save() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            viewModelScope.launch { _event.emit(AddEditRecurringEvent.ShowError("Invalid amount")) }
            return
        }
        if (state.accountId == null) {
            viewModelScope.launch { _event.emit(AddEditRecurringEvent.ShowError("Select an account")) }
            return
        }

        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val recurring = RecurringTransaction(
                    id = if (state.isEditing) editingRecurringId else 0,
                    type = state.type,
                    amount = amount,
                    accountId = state.accountId,
                    toAccountId = if (state.type == TransactionType.TRANSFER) state.toAccountId else null,
                    categoryId = if (state.type != TransactionType.TRANSFER) state.categoryId else null,
                    frequency = state.frequency,
                    nextDate = state.nextDate,
                    isActive = state.isActive,
                    note = state.note
                )

                if (state.isEditing) {
                    recurringRepository.update(recurring)
                } else {
                    recurringRepository.insert(recurring)
                }

                _event.emit(AddEditRecurringEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiState.value = state.copy(isSaving = false)
                _event.emit(AddEditRecurringEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }
}
