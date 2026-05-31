package com.cuangx.finance.feature.portfolio.holding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Account
import com.cuangx.finance.domain.model.AssetType
import com.cuangx.finance.domain.model.Holding
import com.cuangx.finance.domain.repository.AccountRepository
import com.cuangx.finance.domain.repository.HoldingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditHoldingUiState(
    val assetType: AssetType = AssetType.STOCK,
    val ticker: String = "",
    val name: String = "",
    val quantity: String = "",
    val avgBuyPrice: String = "",
    val currency: String = "IDR",
    val fundingAccountId: Long? = null,
    val note: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val accounts: List<Account> = emptyList()
)

sealed class AddEditHoldingEvent {
    data object SaveSuccess : AddEditHoldingEvent()
    data class ShowError(val message: String) : AddEditHoldingEvent()
}

@HiltViewModel
class AddEditHoldingViewModel @Inject constructor(
    private val holdingRepository: HoldingRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditHoldingUiState())
    val uiState: StateFlow<AddEditHoldingUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddEditHoldingEvent>()
    val event: SharedFlow<AddEditHoldingEvent> = _event.asSharedFlow()

    private var editingHoldingId: Long = 0

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            accountRepository.getAllActive().collect { accounts ->
                _uiState.value = _uiState.value.copy(accounts = accounts)
            }
        }
    }

    fun loadHolding(holdingId: Long) {
        viewModelScope.launch {
            val holding = holdingRepository.getByIdOnce(holdingId) ?: return@launch
            editingHoldingId = holding.id
            _uiState.value = _uiState.value.copy(
                assetType = holding.assetType,
                ticker = holding.ticker ?: "",
                name = holding.name,
                quantity = holding.quantity.toLong().toString(),
                avgBuyPrice = holding.avgBuyPrice.toLong().toString(),
                currency = holding.currency,
                fundingAccountId = holding.fundingAccountId,
                note = holding.note,
                isEditing = true
            )
        }
    }

    fun updateAssetType(type: AssetType) { _uiState.value = _uiState.value.copy(assetType = type) }
    fun updateTicker(ticker: String) { _uiState.value = _uiState.value.copy(ticker = ticker) }
    fun updateName(name: String) { _uiState.value = _uiState.value.copy(name = name) }
    fun updateQuantity(qty: String) { _uiState.value = _uiState.value.copy(quantity = qty) }
    fun updateAvgBuyPrice(price: String) { _uiState.value = _uiState.value.copy(avgBuyPrice = price) }
    fun updateCurrency(currency: String) { _uiState.value = _uiState.value.copy(currency = currency) }
    fun updateFundingAccountId(id: Long) { _uiState.value = _uiState.value.copy(fundingAccountId = id) }
    fun updateNote(note: String) { _uiState.value = _uiState.value.copy(note = note) }

    fun save() {
        val state = _uiState.value
        val quantity = state.quantity.toDoubleOrNull()
        val avgBuyPrice = state.avgBuyPrice.toDoubleOrNull()

        if (state.name.isBlank()) {
            viewModelScope.launch { _event.emit(AddEditHoldingEvent.ShowError("Name cannot be empty")) }
            return
        }
        if (quantity == null || quantity <= 0) {
            viewModelScope.launch { _event.emit(AddEditHoldingEvent.ShowError("Invalid quantity")) }
            return
        }
        if (avgBuyPrice == null || avgBuyPrice <= 0) {
            viewModelScope.launch { _event.emit(AddEditHoldingEvent.ShowError("Invalid buy price")) }
            return
        }

        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val holding = Holding(
                    id = if (state.isEditing) editingHoldingId else 0,
                    assetType = state.assetType,
                    ticker = state.ticker.ifBlank { null },
                    name = state.name,
                    quantity = quantity,
                    avgBuyPrice = avgBuyPrice,
                    currency = state.currency,
                    fundingAccountId = state.fundingAccountId,
                    note = state.note
                )

                if (state.isEditing) {
                    holdingRepository.update(holding)
                } else {
                    holdingRepository.insert(holding)
                }

                _event.emit(AddEditHoldingEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiState.value = state.copy(isSaving = false)
                _event.emit(AddEditHoldingEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }
}
