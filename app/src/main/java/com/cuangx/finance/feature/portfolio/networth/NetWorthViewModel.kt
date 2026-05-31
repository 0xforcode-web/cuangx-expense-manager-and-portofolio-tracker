package com.cuangx.finance.feature.portfolio.networth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.usecase.NetWorthSummary
import com.cuangx.finance.domain.usecase.NetWorthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class NetWorthUiState(
    val summary: NetWorthSummary? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class NetWorthViewModel @Inject constructor(
    private val netWorthUseCase: NetWorthUseCase
) : ViewModel() {

    val uiState: StateFlow<NetWorthUiState> = netWorthUseCase.getNetWorthSummary()
        .map { summary ->
            NetWorthUiState(summary = summary, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NetWorthUiState()
        )
}
