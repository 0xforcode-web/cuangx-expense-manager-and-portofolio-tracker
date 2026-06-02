package com.cuangx.finance.feature.expense.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Category
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionType
import com.cuangx.finance.domain.repository.CategoryRepository
import com.cuangx.finance.domain.repository.TransactionRepository
import com.cuangx.finance.core.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
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
    val anchorDate: Long = System.currentTimeMillis(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val categoryExpenses: List<CategoryExpense> = emptyList(),
    val categoryIncome: List<CategoryExpense> = emptyList(),
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val monthlyData: List<MonthlyData> = emptyList(),
    val isLoading: Boolean = true
)

private data class MonthlyStatisticsData(
    val anchorDate: Long,
    val income: Double,
    val expense: Double,
    val transactions: List<Transaction>
)

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _anchorDate = MutableStateFlow(System.currentTimeMillis())
    private val _selectedType = MutableStateFlow(TransactionType.EXPENSE)

    private val monthlyStatistics = _anchorDate.flatMapLatest { anchorDate ->
        val startOfMonth = DateUtils.getStartOfMonth(anchorDate)
        val endOfMonth = DateUtils.getEndOfMonth(anchorDate)

        combine(
            transactionRepository.getTotalIncomeByDateRange(startOfMonth, endOfMonth),
            transactionRepository.getTotalExpenseByDateRange(startOfMonth, endOfMonth),
            transactionRepository.getByDateRange(startOfMonth, endOfMonth)
        ) { income, expense, transactions ->
            MonthlyStatisticsData(
                anchorDate = anchorDate,
                income = income,
                expense = expense,
                transactions = transactions
            )
        }
    }

    val uiState: StateFlow<StatisticsUiState> = combine(
        monthlyStatistics,
        categoryRepository.getAll(),
        _selectedType
    ) { monthlyData, categories, selectedType ->
        val categoryExpenses = CategoryBreakdownCalculator.calculate(
            transactions = monthlyData.transactions,
            categories = categories,
            type = TransactionType.EXPENSE
        )
        val categoryIncome = CategoryBreakdownCalculator.calculate(
            transactions = monthlyData.transactions,
            categories = categories,
            type = TransactionType.INCOME
        )

        StatisticsUiState(
            anchorDate = monthlyData.anchorDate,
            totalIncome = monthlyData.income,
            totalExpense = monthlyData.expense,
            categoryExpenses = categoryExpenses,
            categoryIncome = categoryIncome,
            selectedType = selectedType,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState()
    )

    fun updateSelectedType(type: TransactionType) {
        _selectedType.value = type
    }

    fun goToPreviousMonth() {
        shiftMonth(-1)
    }

    fun goToNextMonth() {
        shiftMonth(1)
    }

    fun goToCurrentMonth() {
        _anchorDate.value = System.currentTimeMillis()
    }

    private fun shiftMonth(amount: Int) {
        _anchorDate.value = Calendar.getInstance().apply {
            timeInMillis = _anchorDate.value
            add(Calendar.MONTH, amount)
        }.timeInMillis
    }
}
