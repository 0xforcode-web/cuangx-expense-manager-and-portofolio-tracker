package com.cuangx.finance.feature.debt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.cuangx.finance.core.ui.components.CalmCard
import com.cuangx.finance.core.ui.components.EmptyDebts
import com.cuangx.finance.core.ui.components.FinanceListRow
import com.cuangx.finance.core.ui.components.SectionHeader
import com.cuangx.finance.core.ui.theme.CuangXSpacing
import com.cuangx.finance.core.ui.theme.DebtColor
import com.cuangx.finance.core.ui.theme.IncomeColor
import com.cuangx.finance.core.util.CurrencyFormatter
import com.cuangx.finance.core.util.DateUtils
import com.cuangx.finance.domain.model.DebtReceivable
import com.cuangx.finance.domain.model.DebtStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtListScreen(
    onNavigateToAddDebt: () -> Unit,
    onNavigateToDebtDetail: (Long) -> Unit,
    viewModel: DebtListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Utang & Piutang") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddDebt,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
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
            item {
                SummaryRow(
                    totalDebt = uiState.totalDebt,
                    totalReceivable = uiState.totalReceivable
                )
            }

            if (uiState.debts.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(CuangXSpacing.xs))
                    SectionHeader(title = "Utang (Harus Bayar)")
                }
                items(uiState.debts, key = { it.id }) { debt ->
                    DebtItem(
                        debt = debt,
                        onClick = { onNavigateToDebtDetail(debt.id) }
                    )
                }
            }

            if (uiState.receivables.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(CuangXSpacing.xs))
                    SectionHeader(title = "Piutang (Akan Diterima)")
                }
                items(uiState.receivables, key = { it.id }) { receivable ->
                    DebtItem(
                        debt = receivable,
                        onClick = { onNavigateToDebtDetail(receivable.id) }
                    )
                }
            }

            if (uiState.debts.isEmpty() && uiState.receivables.isEmpty()) {
                item {
                    EmptyDebts(onAddClick = onNavigateToAddDebt)
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(totalDebt: Double, totalReceivable: Double) {
    CalmCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total Utang",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = CurrencyFormatter.formatIDR(totalDebt),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = DebtColor
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total Piutang",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = CurrencyFormatter.formatIDR(totalReceivable),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = IncomeColor
                )
            }
        }
    }
}

@Composable
private fun DebtItem(
    debt: DebtReceivable,
    onClick: () -> Unit
) {
    val statusText = if (debt.status == DebtStatus.PAID) {
        "LUNAS"
    } else {
        debt.dueDate?.let { "Due: ${DateUtils.formatDate(it)}" } ?: ""
    }

    FinanceListRow(
        icon = Icons.Default.Add,
        iconTint = if (debt.status == DebtStatus.PAID) IncomeColor else DebtColor,
        title = debt.partyName,
        subtitle = statusText,
        trailing = {
            Text(
                text = CurrencyFormatter.formatIDR(debt.remainingAmount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (debt.status == DebtStatus.PAID) IncomeColor else DebtColor
            )
        },
        onClick = onClick
    )
}
