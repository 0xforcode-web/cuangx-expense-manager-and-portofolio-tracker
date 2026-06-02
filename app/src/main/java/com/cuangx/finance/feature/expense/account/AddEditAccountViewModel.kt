package com.cuangx.finance.feature.expense.account

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Account
import com.cuangx.finance.domain.model.AccountType
import com.cuangx.finance.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditAccountUiState(
    val name: String = "",
    val type: AccountType = AccountType.BANK,
    val balance: String = "",
    val currency: String = "IDR",
    val icon: String = "ic_wallet",
    val color: Long = 0xFF4CAF50,
    val creditLimit: String = "",
    val settlementDay: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false
)

sealed class AddEditAccountEvent {
    data object SaveSuccess : AddEditAccountEvent()
    data class ShowError(val message: String) : AddEditAccountEvent()
}

@HiltViewModel
class AddEditAccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditAccountUiState())
    val uiState: StateFlow<AddEditAccountUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddEditAccountEvent>()
    val event: SharedFlow<AddEditAccountEvent> = _event.asSharedFlow()

    private var editingAccountId: Long = 0

    init {
        val accountId = savedStateHandle.get<Long>("accountId")
        if (accountId != null && accountId > 0) {
            loadAccount(accountId)
        }
    }

    private fun loadAccount(accountId: Long) {
        viewModelScope.launch {
            val account = accountRepository.getByIdOnce(accountId) ?: return@launch
            editingAccountId = account.id
            _uiState.value = AddEditAccountUiState(
                name = account.name,
                type = account.type,
                balance = if (account.balance == account.balance.toLong().toDouble()) {
                    account.balance.toLong().toString()
                } else {
                    account.balance.toString()
                },
                currency = account.currency,
                icon = account.icon,
                color = account.color,
                creditLimit = account.creditLimit?.let {
                    if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString()
                } ?: "",
                settlementDay = account.settlementDay?.toString() ?: "",
                isEditing = true
            )
        }
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateType(type: AccountType) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun updateBalance(balance: String) {
        _uiState.value = _uiState.value.copy(balance = balance)
    }

    fun updateCurrency(currency: String) {
        _uiState.value = _uiState.value.copy(currency = currency)
    }

    fun updateIcon(icon: String) {
        _uiState.value = _uiState.value.copy(icon = icon)
    }

    fun updateColor(color: Long) {
        _uiState.value = _uiState.value.copy(color = color)
    }

    fun updateCreditLimit(limit: String) {
        _uiState.value = _uiState.value.copy(creditLimit = limit)
    }

    fun updateSettlementDay(day: String) {
        _uiState.value = _uiState.value.copy(settlementDay = day)
    }

    fun save() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            viewModelScope.launch { _event.emit(AddEditAccountEvent.ShowError("Name cannot be empty")) }
            return
        }

        val balance = state.balance.toDoubleOrNull() ?: 0.0
        val creditLimit = state.creditLimit.toDoubleOrNull()
        val settlementDay = state.settlementDay.toIntOrNull()

        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val account = Account(
                    id = if (state.isEditing) editingAccountId else 0,
                    name = state.name,
                    type = state.type,
                    balance = balance,
                    currency = state.currency,
                    icon = state.icon,
                    color = state.color,
                    creditLimit = if (state.type == AccountType.CREDIT_CARD) creditLimit else null,
                    settlementDay = if (state.type == AccountType.CREDIT_CARD) settlementDay else null
                )

                if (state.isEditing) {
                    accountRepository.update(account)
                } else {
                    accountRepository.insert(account)
                }

                _event.emit(AddEditAccountEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiState.value = state.copy(isSaving = false)
                _event.emit(AddEditAccountEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }
}
