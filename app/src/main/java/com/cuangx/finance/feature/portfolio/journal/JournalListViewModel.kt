package com.cuangx.finance.feature.portfolio.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.JournalAction
import com.cuangx.finance.domain.model.JournalEntry
import com.cuangx.finance.domain.repository.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JournalListUiState(
    val entries: List<JournalEntry> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class JournalListViewModel @Inject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {

    val uiState: StateFlow<JournalListUiState> = journalRepository.getAll()
        .map { entries ->
            JournalListUiState(entries = entries, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = JournalListUiState()
        )

    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch {
            journalRepository.delete(entry)
        }
    }
}
