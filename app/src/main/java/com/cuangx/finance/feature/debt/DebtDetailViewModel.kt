package com.cuangx.finance.feature.debt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Account
import com.cuangx.finance.domain.model.DebtReceivable
import com.cuangx.finance.domain.model.DebtReceivablePayment
import com.cuangx.finance.domain.model.DebtReceivableType
import com.cuangx.finance.domain.model.DebtStatus
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionSource
import com.cuangx.finance.domain.model.TransactionType
import com.cuangx.finance.domain.repository.AccountRepository
import com.cuangx.finance.domain.repository.DebtReceivablePaymentRepository
import com.cuangx.finance.domain.repository.DebtReceivableRepository
import com.cuangx.finance.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DebtDetailUiState(
    val debt: DebtReceivable? = null,
    val payments: List<DebtReceivablePayment> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val totalPaid: Double = 0.0,
    val remaining: Double = 0.0,
    val percentage: Int = 0,
    val isLoading: Boolean = true
)

sealed class DebtDetailEvent {
    data object PaymentSuccess : DebtDetailEvent()
    data class ShowError(val message: String) : DebtDetailEvent()
    data object Deleted : DebtDetailEvent()
}

@HiltViewModel
class DebtDetailViewModel @Inject constructor(
    private val debtRepository: DebtReceivableRepository,
    private val paymentRepository: DebtReceivablePaymentRepository,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DebtDetailUiState())
    val uiState: StateFlow<DebtDetailUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<DebtDetailEvent>()
    val event: SharedFlow<DebtDetailEvent> = _event.asSharedFlow()

    private var currentDebtId: Long = 0

    fun loadDebt(debtId: Long) {
        currentDebtId = debtId
        viewModelScope.launch {
            combine(
                debtRepository.getById(debtId),
                paymentRepository.getByDebtId(debtId),
                accountRepository.getAllActive()
            ) { debt, payments, accounts ->
                val totalPaid = payments.sumOf { it.amount }
                val remaining = debt?.remainingAmount ?: 0.0
                val originalAmount = debt?.originalAmount ?: 1.0
                val percentage = ((totalPaid / originalAmount) * 100).toInt().coerceIn(0, 100)

                DebtDetailUiState(
                    debt = debt,
                    payments = payments,
                    accounts = accounts,
                    totalPaid = totalPaid,
                    remaining = remaining,
                    percentage = percentage,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun makePayment(amount: Double, accountId: Long) {
        val debt = _uiState.value.debt ?: return

        if (amount <= 0) {
            viewModelScope.launch { _event.emit(DebtDetailEvent.ShowError("Invalid amount")) }
            return
        }
        if (amount > debt.remainingAmount) {
            viewModelScope.launch { _event.emit(DebtDetailEvent.ShowError("Amount exceeds remaining")) }
            return
        }

        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()

                val transactionType = if (debt.type == DebtReceivableType.DEBT) {
                    TransactionType.EXPENSE
                } else {
                    TransactionType.INCOME
                }

                val transactionId = transactionRepository.insert(
                    Transaction(
                        type = transactionType,
                        amount = amount,
                        accountId = accountId,
                        date = now,
                        note = "Bayar ${if (debt.type == DebtReceivableType.DEBT) "utang" else "piutang"}: ${debt.partyName}",
                        linkedDebtId = if (debt.type == DebtReceivableType.DEBT) debt.id else null,
                        linkedReceivableId = if (debt.type == DebtReceivableType.RECEIVABLE) debt.id else null,
                        source = TransactionSource.DEBT
                    )
                )

                paymentRepository.insert(
                    DebtReceivablePayment(
                        debtId = debt.id,
                        amount = amount,
                        accountId = accountId,
                        transactionId = transactionId,
                        date = now
                    )
                )

                val newRemaining = debt.remainingAmount - amount
                val newStatus = if (newRemaining <= 0) DebtStatus.PAID else DebtStatus.ACTIVE
                debtRepository.updateRemaining(debt.id, newRemaining, newStatus)

                _event.emit(DebtDetailEvent.PaymentSuccess)
            } catch (e: Exception) {
                _event.emit(DebtDetailEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    fun deleteDebt() {
        viewModelScope.launch {
            val debt = _uiState.value.debt ?: return@launch
            debtRepository.delete(debt)
            _event.emit(DebtDetailEvent.Deleted)
        }
    }
}
