package com.cuangx.finance.feature.expense.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Budget
import com.cuangx.finance.domain.model.BudgetPeriod
import com.cuangx.finance.domain.model.Category
import com.cuangx.finance.domain.model.TransactionType
import com.cuangx.finance.domain.repository.BudgetRepository
import com.cuangx.finance.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditBudgetUiState(
    val categoryId: Long? = null,
    val amount: String = "",
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val startDate: Long = System.currentTimeMillis(),
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val categories: List<Category> = emptyList()
)

sealed class AddEditBudgetEvent {
    data object SaveSuccess : AddEditBudgetEvent()
    data class ShowError(val message: String) : AddEditBudgetEvent()
}

@HiltViewModel
class AddEditBudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditBudgetUiState())
    val uiState: StateFlow<AddEditBudgetUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddEditBudgetEvent>()
    val event: SharedFlow<AddEditBudgetEvent> = _event.asSharedFlow()

    private var editingBudgetId: Long = 0

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getByType(TransactionType.EXPENSE).collect { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
            }
        }
    }

    fun loadBudget(budgetId: Long) {
        viewModelScope.launch {
            val budget = budgetRepository.getByIdOnce(budgetId) ?: return@launch
            editingBudgetId = budget.id
            _uiState.value = _uiState.value.copy(
                categoryId = budget.categoryId,
                amount = budget.amount.toLong().toString(),
                period = budget.period,
                startDate = budget.startDate,
                isEditing = true
            )
        }
    }

    fun updateCategoryId(categoryId: Long) {
        _uiState.value = _uiState.value.copy(categoryId = categoryId)
    }

    fun updateAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun updatePeriod(period: BudgetPeriod) {
        _uiState.value = _uiState.value.copy(period = period)
    }

    fun save() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull()

        if (state.categoryId == null) {
            viewModelScope.launch { _event.emit(AddEditBudgetEvent.ShowError("Select a category")) }
            return
        }

        if (amount == null || amount <= 0) {
            viewModelScope.launch { _event.emit(AddEditBudgetEvent.ShowError("Invalid amount")) }
            return
        }

        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val budget = Budget(
                    id = if (state.isEditing) editingBudgetId else 0,
                    categoryId = state.categoryId,
                    amount = amount,
                    period = state.period,
                    startDate = state.startDate
                )

                if (state.isEditing) {
                    budgetRepository.update(budget)
                } else {
                    budgetRepository.insert(budget)
                }

                _event.emit(AddEditBudgetEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiState.value = state.copy(isSaving = false)
                _event.emit(AddEditBudgetEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }
}
