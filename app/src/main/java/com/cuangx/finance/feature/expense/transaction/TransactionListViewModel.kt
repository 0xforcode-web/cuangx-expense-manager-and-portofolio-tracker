package com.cuangx.finance.feature.expense.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionType
import com.cuangx.finance.domain.repository.TransactionRepository
import com.cuangx.finance.core.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionListUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val selectedMode: ExpenseViewMode = ExpenseViewMode.DAILY,
    val anchorDate: Long = System.currentTimeMillis(),
    val dailyGroups: List<TransactionDayGroup> = emptyList(),
    val noteGroups: List<TransactionDayGroup> = emptyList(),
    val calendarCells: List<TransactionCalendarCell> = emptyList(),
    val monthlySummaries: List<TransactionMonthSummary> = emptyList(),
    val totalSummary: TransactionTotalSummary = TransactionTotalSummary(
        income = 0.0,
        expense = 0.0,
        balance = 0.0,
        transactionCount = 0,
    ),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedType: TransactionType? = null,
    val selectedCategoryId: Long? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val now = System.currentTimeMillis()

    private val _selectedMode = MutableStateFlow(ExpenseViewMode.DAILY)
    private val _anchorDate = MutableStateFlow(now)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedType = MutableStateFlow<TransactionType?>(null)
    private val _selectedCategoryId = MutableStateFlow<Long?>(null)

    private val activeRange = combine(_selectedMode, _anchorDate) { mode, anchorDate ->
        DateRange.forMode(mode, anchorDate)
    }

    private val allTransactions = activeRange.flatMapLatest { range ->
        transactionRepository.getByDateRange(range.startDate, range.endDate)
    }
    private val totalIncome = activeRange.flatMapLatest { range ->
        transactionRepository.getTotalIncomeByDateRange(range.startDate, range.endDate)
    }
    private val totalExpense = activeRange.flatMapLatest { range ->
        transactionRepository.getTotalExpenseByDateRange(range.startDate, range.endDate)
    }
    private val filters = combine(
        _searchQuery,
        _selectedType,
        _selectedCategoryId,
        _selectedMode,
        _anchorDate
    ) { query, type, categoryId, mode, anchorDate ->
        TransactionFilters(
            query = query,
            type = type,
            categoryId = categoryId,
            mode = mode,
            anchorDate = anchorDate
        )
    }

    val uiState: StateFlow<TransactionListUiState> = combine(
        allTransactions,
        totalIncome,
        totalExpense,
        filters
    ) { transactions, income, expense, filters ->
        val filtered = transactions.filter { transaction ->
            val matchesQuery = filters.query.isBlank() ||
                transaction.note.contains(filters.query, ignoreCase = true)
            val matchesType = filters.type == null || transaction.type == filters.type
            val matchesCategory = filters.categoryId == null || transaction.categoryId == filters.categoryId
            matchesQuery && matchesType && matchesCategory
        }
        val anchorYear = Calendar.getInstance().apply {
            timeInMillis = filters.anchorDate
        }.get(Calendar.YEAR)

        TransactionListUiState(
            transactions = filtered,
            totalIncome = income,
            totalExpense = expense,
            selectedMode = filters.mode,
            anchorDate = filters.anchorDate,
            dailyGroups = TransactionSummaryCalculator.dailyGroups(filtered),
            noteGroups = TransactionSummaryCalculator.noteGroups(filtered),
            calendarCells = TransactionSummaryCalculator.calendarCells(filters.anchorDate, filtered),
            monthlySummaries = TransactionSummaryCalculator.monthlySummaries(anchorYear, filtered),
            totalSummary = TransactionSummaryCalculator.totalSummary(filtered),
            isLoading = false,
            searchQuery = filters.query,
            selectedType = filters.type,
            selectedCategoryId = filters.categoryId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionListUiState()
    )

    val selectedMode: StateFlow<ExpenseViewMode> = _selectedMode.asStateFlow()
    val anchorDate: StateFlow<Long> = _anchorDate.asStateFlow()

    fun updateSelectedMode(mode: ExpenseViewMode) {
        _selectedMode.value = mode
    }

    fun goToPreviousPeriod() {
        shiftAnchorDate(-1)
    }

    fun goToNextPeriod() {
        shiftAnchorDate(1)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedType(type: TransactionType?) {
        _selectedType.value = if (_selectedType.value == type) null else type
    }

    fun updateSelectedCategoryId(categoryId: Long?) {
        _selectedCategoryId.value = if (_selectedCategoryId.value == categoryId) null else categoryId
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedType.value = null
        _selectedCategoryId.value = null
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.delete(transaction)
        }
    }

    private fun shiftAnchorDate(amount: Int) {
        val field = when (_selectedMode.value) {
            ExpenseViewMode.DAILY,
            ExpenseViewMode.CALENDAR,
            ExpenseViewMode.NOTE -> Calendar.MONTH
            ExpenseViewMode.MONTHLY,
            ExpenseViewMode.TOTAL -> Calendar.YEAR
        }
        _anchorDate.value = Calendar.getInstance().apply {
            timeInMillis = _anchorDate.value
            add(field, amount)
        }.timeInMillis
    }

    private data class DateRange(
        val startDate: Long,
        val endDate: Long,
    ) {
        companion object {
            fun forMode(mode: ExpenseViewMode, anchorDate: Long): DateRange {
                return when (mode) {
                    ExpenseViewMode.DAILY,
                    ExpenseViewMode.CALENDAR,
                    ExpenseViewMode.NOTE -> DateRange(
                        startDate = DateUtils.getStartOfMonth(anchorDate),
                        endDate = DateUtils.getEndOfMonth(anchorDate),
                    )
                    ExpenseViewMode.MONTHLY,
                    ExpenseViewMode.TOTAL -> DateRange(
                        startDate = DateUtils.getStartOfYear(anchorDate),
                        endDate = DateUtils.getEndOfYear(anchorDate),
                    )
                }
            }
        }
    }

    private data class TransactionFilters(
        val query: String,
        val type: TransactionType?,
        val categoryId: Long?,
        val mode: ExpenseViewMode,
        val anchorDate: Long
    )
}
