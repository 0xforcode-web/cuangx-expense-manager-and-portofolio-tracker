package com.cuangx.finance.feature.expense.recurring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuangx.finance.core.ui.components.CalmCard
import com.cuangx.finance.core.ui.components.FinanceListRow
import com.cuangx.finance.core.ui.theme.CuangXSpacing
import com.cuangx.finance.core.ui.theme.ExpenseColor
import com.cuangx.finance.core.ui.theme.IncomeColor
import com.cuangx.finance.core.ui.theme.TransferColor
import com.cuangx.finance.core.util.CurrencyFormatter
import com.cuangx.finance.core.util.DateUtils
import com.cuangx.finance.domain.model.RecurringTransaction
import com.cuangx.finance.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringScreen(
    onNavigateToAddRecurring: () -> Unit,
    viewModel: RecurringViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recurring Transactions") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddRecurring,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Recurring")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(CuangXSpacing.xs),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(CuangXSpacing.md)
        ) {
            items(uiState.recurringList, key = { it.id }) { recurring ->
                RecurringItem(
                    recurring = recurring,
                    onToggleActive = { viewModel.toggleActive(recurring) }
                )
            }
        }
    }
}

@Composable
private fun RecurringItem(
    recurring: RecurringTransaction,
    onToggleActive: () -> Unit
) {
    val typeColor = when (recurring.type) {
        TransactionType.INCOME -> IncomeColor
        TransactionType.EXPENSE -> ExpenseColor
        TransactionType.TRANSFER -> TransferColor
    }

    FinanceListRow(
        icon = when (recurring.type) {
            TransactionType.INCOME -> Icons.Default.Add
            TransactionType.EXPENSE -> Icons.Default.Add
            TransactionType.TRANSFER -> Icons.Default.Add
        },
        iconTint = typeColor,
        title = recurring.note.ifEmpty { recurring.type.displayName },
        subtitle = "${recurring.frequency.displayName} • Next: ${DateUtils.formatDate(recurring.nextDate)}",
        trailing = {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text(
                    text = "${if (recurring.type == TransactionType.INCOME) "+" else "-"}${CurrencyFormatter.formatIDR(recurring.amount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = typeColor
                )
                Switch(
                    checked = recurring.isActive,
                    onCheckedChange = { onToggleActive() }
                )
            }
        }
    )
}
