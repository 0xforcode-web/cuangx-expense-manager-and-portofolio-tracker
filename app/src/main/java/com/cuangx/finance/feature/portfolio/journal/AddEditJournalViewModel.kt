package com.cuangx.finance.feature.portfolio.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Account
import com.cuangx.finance.domain.model.AssetType
import com.cuangx.finance.domain.model.JournalAction
import com.cuangx.finance.domain.model.JournalEntry
import com.cuangx.finance.domain.repository.AccountRepository
import com.cuangx.finance.domain.repository.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditJournalUiState(
    val action: JournalAction = JournalAction.BUY,
    val assetType: AssetType = AssetType.STOCK,
    val ticker: String = "",
    val name: String = "",
    val quantity: String = "",
    val price: String = "",
    val fee: String = "0",
    val accountId: Long? = null,
    val reason: String = "",
    val tags: String = "",
    val date: Long = System.currentTimeMillis(),
    val note: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val accounts: List<Account> = emptyList(),
    val existingHoldings: List<com.cuangx.finance.domain.model.Holding> = emptyList()
)

sealed class AddEditJournalEvent {
    data object SaveSuccess : AddEditJournalEvent()
    data class ShowError(val message: String) : AddEditJournalEvent()
}

@HiltViewModel
class AddEditJournalViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val accountRepository: AccountRepository,
    private val holdingRepository: com.cuangx.finance.domain.repository.HoldingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditJournalUiState())
    val uiState: StateFlow<AddEditJournalUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddEditJournalEvent>()
    val event: SharedFlow<AddEditJournalEvent> = _event.asSharedFlow()

    private var editingJournalId: Long = 0

    init {
        loadAccounts()
        loadHoldings()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            accountRepository.getAllActive().collect { accounts ->
                _uiState.value = _uiState.value.copy(accounts = accounts)
            }
        }
    }

    private fun loadHoldings() {
        viewModelScope.launch {
            holdingRepository.getAll().collect { holdings ->
                _uiState.value = _uiState.value.copy(existingHoldings = holdings)
            }
        }
    }

    fun loadJournal(journalId: Long) {
        viewModelScope.launch {
            val entry = journalRepository.getByIdOnce(journalId) ?: return@launch
            editingJournalId = entry.id
            _uiState.value = _uiState.value.copy(
                action = entry.action,
                assetType = entry.assetType,
                ticker = entry.ticker ?: "",
                name = entry.name,
                quantity = entry.quantity.toLong().toString(),
                price = entry.price.toLong().toString(),
                fee = entry.fee.toLong().toString(),
                accountId = entry.accountId,
                reason = entry.reason,
                tags = entry.tags,
                date = entry.date,
                note = entry.note,
                isEditing = true
            )
        }
    }

    fun updateAction(action: JournalAction) { _uiState.value = _uiState.value.copy(action = action) }
    fun updateAssetType(type: AssetType) { _uiState.value = _uiState.value.copy(assetType = type) }
    fun updateTicker(ticker: String) { _uiState.value = _uiState.value.copy(ticker = ticker) }
    fun updateName(name: String) { _uiState.value = _uiState.value.copy(name = name) }
    fun updateQuantity(qty: String) { _uiState.value = _uiState.value.copy(quantity = qty) }
    fun updatePrice(price: String) { _uiState.value = _uiState.value.copy(price = price) }
    fun updateFee(fee: String) { _uiState.value = _uiState.value.copy(fee = fee) }
    fun updateAccountId(id: Long) { _uiState.value = _uiState.value.copy(accountId = id) }
    fun updateReason(reason: String) { _uiState.value = _uiState.value.copy(reason = reason) }
    fun updateTags(tags: String) { _uiState.value = _uiState.value.copy(tags = tags) }
    fun updateDate(date: Long) { _uiState.value = _uiState.value.copy(date = date) }
    fun updateNote(note: String) { _uiState.value = _uiState.value.copy(note = note) }

    fun deleteJournal() {
        if (!uiState.value.isEditing) return

        _uiState.value = _uiState.value.copy(isSaving = true)
        viewModelScope.launch {
            try {
                journalRepository.deleteById(editingJournalId)
                _event.emit(AddEditJournalEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false)
                _event.emit(AddEditJournalEvent.ShowError(e.message ?: "Gagal menghapus journal"))
            }
        }
    }

    fun save() {
        val state = _uiState.value
        val quantity = state.quantity.toDoubleOrNull()
        val price = state.price.toDoubleOrNull()
        val fee = state.fee.toDoubleOrNull() ?: 0.0

        if (state.name.isBlank()) {
            viewModelScope.launch { _event.emit(AddEditJournalEvent.ShowError("Nama aset tidak boleh kosong")) }
            return
        }
        if (quantity == null || quantity <= 0 || quantity.isInfinite() || quantity.isNaN()) {
            viewModelScope.launch { _event.emit(AddEditJournalEvent.ShowError("Jumlah tidak valid")) }
            return
        }
        if (price == null || price <= 0 || price.isInfinite() || price.isNaN()) {
            viewModelScope.launch { _event.emit(AddEditJournalEvent.ShowError("Harga tidak valid")) }
            return
        }
        if (state.accountId == null) {
            viewModelScope.launch { _event.emit(AddEditJournalEvent.ShowError("Pilih akun sumber dana")) }
            return
        }

        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val totalAmount = (quantity * price) + fee
                if (state.action == JournalAction.BUY) {
                    val currentBalance = accountRepository.getBalance(state.accountId) ?: 0.0
                    var balanceToConsider = currentBalance

                    if (state.isEditing) {
                        val oldEntry = journalRepository.getByIdOnce(editingJournalId)
                        if (oldEntry != null && oldEntry.accountId == state.accountId) {
                            val oldAmount = when (oldEntry.action) {
                                JournalAction.BUY -> oldEntry.totalAmount
                                JournalAction.SELL -> (oldEntry.quantity * oldEntry.price) - oldEntry.fee
                                JournalAction.DIVIDEND -> oldEntry.quantity * oldEntry.price
                            }
                            // Add back the old amount if it was a BUY to check if we can afford the new one
                            if (oldEntry.action == JournalAction.BUY) {
                                balanceToConsider += oldAmount
                            }
                        }
                    }

                    if (balanceToConsider < totalAmount) {
                        _uiState.value = _uiState.value.copy(isSaving = false)
                        _event.emit(AddEditJournalEvent.ShowError("Saldo tidak cukup"))
                        return@launch
                    }
                }

                val entry = JournalEntry(
                    id = if (state.isEditing) editingJournalId else 0,
                    action = state.action,
                    assetType = state.assetType,
                    ticker = state.ticker.ifBlank { null },
                    name = state.name,
                    quantity = quantity,
                    price = price,
                    fee = fee,
                    accountId = state.accountId,
                    reason = state.reason,
                    tags = state.tags,
                    date = state.date,
                    note = state.note
                )

                if (state.isEditing) {
                    journalRepository.update(entry)
                } else {
                    journalRepository.insert(entry)
                }

                _event.emit(AddEditJournalEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false)
                _event.emit(AddEditJournalEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }
}
