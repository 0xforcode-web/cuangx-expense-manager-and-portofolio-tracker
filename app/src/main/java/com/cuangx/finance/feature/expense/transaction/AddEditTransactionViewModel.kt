package com.cuangx.finance.feature.expense.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Account
import com.cuangx.finance.domain.model.Category
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionSource
import com.cuangx.finance.domain.model.TransactionType
import com.cuangx.finance.domain.repository.AccountRepository
import com.cuangx.finance.domain.repository.CategoryRepository
import com.cuangx.finance.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditTransactionUiState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val accountId: Long? = null,
    val toAccountId: Long? = null,
    val categoryId: Long? = null,
    val date: Long = System.currentTimeMillis(),
    val note: String = "",
    val isBookmarked: Boolean = false,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList()
)

sealed class AddEditTransactionEvent {
    data object SaveSuccess : AddEditTransactionEvent()
    data class ShowError(val message: String) : AddEditTransactionEvent()
}

@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditTransactionUiState())
    val uiState: StateFlow<AddEditTransactionUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddEditTransactionEvent>()
    val event: SharedFlow<AddEditTransactionEvent> = _event.asSharedFlow()

    private var editingTransactionId: Long = 0

    init {
        loadAccountsAndCategories()
    }

    private fun loadAccountsAndCategories() {
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

    fun loadTransaction(transactionId: Long) {
        viewModelScope.launch {
            val transaction = transactionRepository.getByIdOnce(transactionId) ?: return@launch
            editingTransactionId = transaction.id
            _uiState.value = _uiState.value.copy(
                type = transaction.type,
                amount = transaction.amount.toLong().toString(),
                accountId = transaction.accountId,
                toAccountId = transaction.toAccountId,
                categoryId = transaction.categoryId,
                date = transaction.date,
                note = transaction.note,
                isBookmarked = transaction.isBookmarked,
                isEditing = true
            )
        }
    }

    fun updateType(type: TransactionType) {
        val current = _uiState.value
        _uiState.value = current.copy(
            type = type,
            categoryId = null,
            toAccountId = if (type == TransactionType.TRANSFER) current.toAccountId else null
        )
    }

    fun updateAmount(amount: String) {
        // Remove non-numeric characters except for one decimal point
        val cleanAmount = amount.replace(Regex("[^0-9.]"), "")
        if (cleanAmount.count { it == '.' } > 1) return
        _uiState.value = _uiState.value.copy(amount = cleanAmount)
    }

    fun updateAccountId(accountId: Long) {
        _uiState.value = _uiState.value.copy(accountId = accountId)
    }

    fun updateToAccountId(toAccountId: Long) {
        _uiState.value = _uiState.value.copy(toAccountId = toAccountId)
    }

    fun updateCategoryId(categoryId: Long) {
        _uiState.value = _uiState.value.copy(categoryId = categoryId)
    }

    fun updateDate(date: Long) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun updateNote(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun deleteTransaction() {
        if (!uiState.value.isEditing) return
        
        _uiState.value = _uiState.value.copy(isSaving = true)
        viewModelScope.launch {
            try {
                transactionRepository.deleteById(editingTransactionId)
                _event.emit(AddEditTransactionEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false)
                _event.emit(AddEditTransactionEvent.ShowError(e.message ?: "Gagal menghapus transaksi"))
            }
        }
    }

    fun save() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull()

        if (amount == null || amount <= 0 || amount.isInfinite() || amount.isNaN()) {
            viewModelScope.launch { _event.emit(AddEditTransactionEvent.ShowError("Masukkan nominal yang valid")) }
            return
        }

        if (state.accountId == null) {
            viewModelScope.launch { _event.emit(AddEditTransactionEvent.ShowError("Pilih akun sumber dana")) }
            return
        }

        if (state.type == TransactionType.TRANSFER && state.toAccountId == null) {
            viewModelScope.launch { _event.emit(AddEditTransactionEvent.ShowError("Pilih akun tujuan transfer")) }
            return
        }

        if (state.type != TransactionType.TRANSFER && state.categoryId == null) {
            viewModelScope.launch { _event.emit(AddEditTransactionEvent.ShowError("Pilih kategori transaksi")) }
            return
        }

        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val transaction = Transaction(
                    id = if (state.isEditing) editingTransactionId else 0,
                    type = state.type,
                    amount = amount,
                    accountId = state.accountId,
                    toAccountId = if (state.type == TransactionType.TRANSFER) state.toAccountId else null,
                    categoryId = if (state.type != TransactionType.TRANSFER) state.categoryId else null,
                    date = state.date,
                    note = state.note,
                    photoUri = null,
                    isBookmarked = state.isBookmarked,
                    source = TransactionSource.EXPENSE
                )

                if (state.isEditing) {
                    transactionRepository.update(transaction)
                } else {
                    transactionRepository.insert(transaction)
                }

                _event.emit(AddEditTransactionEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiState.value = state.copy(isSaving = false)
                _event.emit(AddEditTransactionEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }
}
