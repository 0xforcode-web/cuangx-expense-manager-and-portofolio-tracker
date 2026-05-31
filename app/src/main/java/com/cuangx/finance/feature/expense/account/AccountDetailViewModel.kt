package com.cuangx.finance.feature.expense.account

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Account
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.repository.AccountRepository
import com.cuangx.finance.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountDetailUiState(
    val account: Account? = null,
    val recentTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed class AccountDetailEvent {
    data object DeleteSuccess : AccountDetailEvent()
    data class ShowError(val message: String) : AccountDetailEvent()
}

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val accountId: Long = savedStateHandle.get<Long>("accountId") ?: 0L

    private val _uiState = MutableStateFlow(AccountDetailUiState())
    val uiState: StateFlow<AccountDetailUiState> = _uiState.asStateFlow()

    private val _event = MutableStateFlow<AccountDetailEvent?>(null)
    val event: StateFlow<AccountDetailEvent?> = _event.asStateFlow()

    init {
        loadAccount()
        loadRecentTransactions()
    }

    private fun loadAccount() {
        viewModelScope.launch {
            accountRepository.getById(accountId)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load account"
                    )
                }
                .collect { account ->
                    _uiState.value = _uiState.value.copy(
                        account = account,
                        isLoading = false
                    )
                }
        }
    }

    private fun loadRecentTransactions() {
        viewModelScope.launch {
            transactionRepository.getByAccountId(accountId)
                .catch { /* silently handle */ }
                .collect { transactions ->
                    _uiState.value = _uiState.value.copy(
                        recentTransactions = transactions.take(20)
                    )
                }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                val account = _uiState.value.account ?: return@launch
                accountRepository.delete(account)
                _event.value = AccountDetailEvent.DeleteSuccess
            } catch (e: Exception) {
                _event.value = AccountDetailEvent.ShowError(e.message ?: "Failed to delete")
            }
        }
    }

    fun archiveAccount() {
        viewModelScope.launch {
            try {
                accountRepository.archive(accountId)
                _event.value = AccountDetailEvent.DeleteSuccess
            } catch (e: Exception) {
                _event.value = AccountDetailEvent.ShowError(e.message ?: "Failed to archive")
            }
        }
    }

    fun clearEvent() {
        _event.value = null
    }
}
