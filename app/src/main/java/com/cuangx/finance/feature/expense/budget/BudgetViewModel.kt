package com.cuangx.finance.feature.expense.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Budget
import com.cuangx.finance.domain.model.BudgetPeriod
import com.cuangx.finance.domain.model.Category
import com.cuangx.finance.domain.repository.BudgetRepository
import com.cuangx.finance.domain.repository.CategoryRepository
import com.cuangx.finance.domain.repository.TransactionRepository
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

data class BudgetWithProgress(
    val budget: Budget,
    val category: Category?,
    val spent: Double,
    val percentage: Int
)

data class BudgetUiState(
    val budgets: List<BudgetWithProgress> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val now = System.currentTimeMillis()
    private val startOfMonth = DateUtils.getStartOfMonth(now)
    private val endOfMonth = DateUtils.getEndOfMonth(now)

    val uiState: StateFlow<BudgetUiState> = combine(
        budgetRepository.getAllActive(),
        categoryRepository.getAll()
    ) { budgets, categories ->
        val budgetsWithProgress = budgets.map { budget ->
            val category = categories.find { it.id == budget.categoryId }
            val spent = transactionRepository.getExpenseByCategoryAndDateRange(
                budget.categoryId, startOfMonth, endOfMonth
            )
            val percentage = if (budget.amount > 0) {
                ((spent / budget.amount) * 100).toInt().coerceIn(0, 999)
            } else 0

            BudgetWithProgress(
                budget = budget,
                category = category,
                spent = spent,
                percentage = percentage
            )
        }
        BudgetUiState(budgets = budgetsWithProgress, isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudgetUiState()
    )

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            budgetRepository.delete(budget)
        }
    }
}
