package com.cuangx.finance.feature.expense.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Transaction
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
    val isLoading: Boolean = true
)

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val now = System.currentTimeMillis()
    private val startOfMonth = DateUtils.getStartOfMonth(now)
    private val endOfMonth = DateUtils.getEndOfMonth(now)

    val uiState: StateFlow<TransactionListUiState> = combine(
        transactionRepository.getByDateRange(startOfMonth, endOfMonth),
        transactionRepository.getTotalIncomeByDateRange(startOfMonth, endOfMonth),
        transactionRepository.getTotalExpenseByDateRange(startOfMonth, endOfMonth)
    ) { transactions, income, expense ->
        TransactionListUiState(
            transactions = transactions,
            totalIncome = income,
            totalExpense = expense,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionListUiState()
    )

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.delete(transaction)
        }
    }
}
