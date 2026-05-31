package com.cuangx.finance.feature.expense.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionType
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

data class TransactionListUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedType: TransactionType? = null,
    val selectedCategoryId: Long? = null
)

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val now = System.currentTimeMillis()
    private val startOfMonth = DateUtils.getStartOfMonth(now)
    private val endOfMonth = DateUtils.getEndOfMonth(now)

    private val _searchQuery = MutableStateFlow("")
    private val _selectedType = MutableStateFlow<TransactionType?>(null)
    private val _selectedCategoryId = MutableStateFlow<Long?>(null)

    private val allTransactions = transactionRepository.getByDateRange(startOfMonth, endOfMonth)
    private val totalIncome = transactionRepository.getTotalIncomeByDateRange(startOfMonth, endOfMonth)
    private val totalExpense = transactionRepository.getTotalExpenseByDateRange(startOfMonth, endOfMonth)

    val uiState: StateFlow<TransactionListUiState> = combine(
        allTransactions,
        totalIncome,
        totalExpense,
        _searchQuery,
        _selectedType
    ) { transactions, income, expense, query, type ->
        val filtered = transactions.filter { transaction ->
            val matchesQuery = query.isBlank() || 
                transaction.note.contains(query, ignoreCase = true)
            val matchesType = type == null || transaction.type == type
            matchesQuery && matchesType
        }

        TransactionListUiState(
            transactions = filtered,
            totalIncome = income,
            totalExpense = expense,
            isLoading = false,
            searchQuery = query,
            selectedType = type,
            selectedCategoryId = _selectedCategoryId.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionListUiState()
    )

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
}
