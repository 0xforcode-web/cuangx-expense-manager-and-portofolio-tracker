package com.cuangx.finance.feature.portfolio.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuangx.finance.core.ui.components.EmptyJournal
import com.cuangx.finance.core.ui.components.FinanceListRow
import com.cuangx.finance.core.ui.theme.ExpenseColor
import com.cuangx.finance.core.ui.theme.IncomeColor
import com.cuangx.finance.core.ui.theme.TransferColor
import com.cuangx.finance.core.util.CurrencyFormatter
import com.cuangx.finance.core.util.DateUtils
import com.cuangx.finance.domain.model.JournalAction
import com.cuangx.finance.domain.model.JournalEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalListScreen(
    onNavigateToAddJournal: () -> Unit,
    onNavigateToEditJournal: (Long) -> Unit,
    viewModel: JournalListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journal") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddJournal,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Journal Entry")
            }
        }
    ) { padding ->
        if (uiState.entries.isEmpty() && !uiState.isLoading) {
            EmptyJournal(onAddClick = onNavigateToAddJournal)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
            ) {
                items(uiState.entries, key = { it.id }) { entry ->
                    JournalEntryItem(
                        entry = entry,
                        onClick = { onNavigateToEditJournal(entry.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun JournalEntryItem(
    entry: JournalEntry,
    onClick: () -> Unit
) {
    val actionColor = when (entry.action) {
        JournalAction.BUY -> ExpenseColor
        JournalAction.SELL -> IncomeColor
        JournalAction.DIVIDEND -> TransferColor
    }
    val actionIcon = when (entry.action) {
        JournalAction.BUY -> Icons.Default.ArrowUpward
        JournalAction.SELL -> Icons.Default.ArrowDownward
        JournalAction.DIVIDEND -> Icons.Default.AttachMoney
    }

    FinanceListRow(
        icon = actionIcon,
        iconTint = actionColor,
        title = "${entry.action.displayName} ${entry.name}",
        subtitle = "${entry.quantity} x ${CurrencyFormatter.formatIDR(entry.price)}",
        supporting = DateUtils.formatDate(entry.date),
        onClick = onClick,
        trailing = {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CurrencyFormatter.formatIDR(entry.quantity * entry.price),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                if (entry.fee > 0) {
                    Text(
                        text = "Fee ${CurrencyFormatter.formatIDR(entry.fee)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}
