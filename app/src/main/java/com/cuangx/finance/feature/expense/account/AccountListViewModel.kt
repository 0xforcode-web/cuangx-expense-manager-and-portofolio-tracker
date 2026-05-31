package com.cuangx.finance.feature.expense.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Account
import com.cuangx.finance.domain.model.AccountType
import com.cuangx.finance.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountListUiState(
    val accounts: List<Account> = emptyList(),
    val totalBalance: Double = 0.0,
    val isLoading: Boolean = true
)

@HiltViewModel
class AccountListViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    val uiState: StateFlow<AccountListUiState> = combine(
        accountRepository.getAllActive(),
        accountRepository.getTotalBalance()
    ) { accounts, totalBalance ->
        AccountListUiState(
            accounts = accounts,
            totalBalance = totalBalance,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AccountListUiState()
    )

    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.delete(account)
        }
    }

    fun archiveAccount(id: Long) {
        viewModelScope.launch {
            accountRepository.archive(id)
        }
    }
}
