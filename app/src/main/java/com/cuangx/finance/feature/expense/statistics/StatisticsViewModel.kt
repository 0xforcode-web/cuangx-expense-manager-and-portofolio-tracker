package com.cuangx.finance.feature.expense.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Category
import com.cuangx.finance.domain.model.TransactionType
import com.cuangx.finance.domain.repository.CategoryRepository
import com.cuangx.finance.domain.repository.TransactionRepository
import com.cuangx.finance.core.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CategoryExpense(
    val category: Category,
    val amount: Double,
    val percentage: Double
)

data class MonthlyData(
    val month: String,
    val income: Double,
    val expense: Double
)

data class StatisticsUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val categoryExpenses: List<CategoryExpense> = emptyList(),
    val categoryIncome: List<CategoryExpense> = emptyList(),
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val monthlyData: List<MonthlyData> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val now = System.currentTimeMillis()
    private val startOfMonth = DateUtils.getStartOfMonth(now)
    private val endOfMonth = DateUtils.getEndOfMonth(now)
    private val selectedType = MutableStateFlow(TransactionType.EXPENSE)

    val uiState: StateFlow<StatisticsUiState> = combine(
        transactionRepository.getTotalIncomeByDateRange(startOfMonth, endOfMonth),
        transactionRepository.getTotalExpenseByDateRange(startOfMonth, endOfMonth),
        transactionRepository.getByDateRange(startOfMonth, endOfMonth),
        categoryRepository.getAll(),
        selectedType
    ) { income, expense, transactions, categories, selectedType ->
        StatisticsUiState(
            totalIncome = income,
            totalExpense = expense,
            categoryExpenses = CategoryBreakdownCalculator.calculate(
                transactions = transactions,
                categories = categories,
                type = TransactionType.EXPENSE
            ),
            categoryIncome = CategoryBreakdownCalculator.calculate(
                transactions = transactions,
                categories = categories,
                type = TransactionType.INCOME
            ),
            selectedType = selectedType,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState()
    )

    fun updateSelectedType(type: TransactionType) {
        selectedType.value = type
    }
}
