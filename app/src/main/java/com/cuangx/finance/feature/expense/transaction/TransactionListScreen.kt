package com.cuangx.finance.feature.expense.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuangx.finance.core.ui.components.CalmCard
import com.cuangx.finance.core.ui.components.FinanceListRow
import com.cuangx.finance.core.ui.components.signedTransactionAmount
import com.cuangx.finance.core.ui.components.transactionAmountColor
import com.cuangx.finance.core.ui.theme.ExpenseColor
import com.cuangx.finance.core.ui.theme.IncomeColor
import com.cuangx.finance.core.ui.theme.TransferColor
import com.cuangx.finance.core.util.CurrencyFormatter
import com.cuangx.finance.core.util.DateUtils
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TransactionListScreen(
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToEditTransaction: (Long) -> Unit,
    viewModel: TransactionListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTransaction,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PeriodNavigation(
                uiState = uiState,
                onPrevious = viewModel::goToPreviousPeriod,
                onNext = viewModel::goToNextPeriod,
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExpenseViewMode.entries.forEach { mode ->
                    FilterChip(
                        selected = uiState.selectedMode == mode,
                        onClick = { viewModel.updateSelectedMode(mode) },
                        label = { Text(mode.label) }
                    )
                }
            }

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                placeholder = { Text("Cari transaksi...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            TypeFilterChips(
                uiState = uiState,
                onSelectedType = viewModel::updateSelectedType,
                onClear = viewModel::clearFilters,
            )

            when (uiState.selectedMode) {
                ExpenseViewMode.DAILY -> DailyContent(uiState, onNavigateToEditTransaction)
                ExpenseViewMode.CALENDAR -> CalendarContent(uiState)
                ExpenseViewMode.MONTHLY -> MonthlyContent(uiState)
                ExpenseViewMode.TOTAL -> TotalContent(uiState)
                ExpenseViewMode.NOTE -> NoteContent(uiState, onNavigateToEditTransaction)
            }
        }
    }
}

@Composable
private fun PeriodNavigation(
    uiState: TransactionListUiState,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous period")
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = periodLabel(uiState.selectedMode, uiState.anchorDate),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = rangeHint(uiState.selectedMode),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onNext) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next period")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TypeFilterChips(
    uiState: TransactionListUiState,
    onSelectedType: (TransactionType?) -> Unit,
    onClear: () -> Unit,
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = uiState.selectedType == TransactionType.INCOME,
            onClick = { onSelectedType(TransactionType.INCOME) },
            label = { Text("Income") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = IncomeColor.copy(alpha = 0.2f)
            )
        )
        FilterChip(
            selected = uiState.selectedType == TransactionType.EXPENSE,
            onClick = { onSelectedType(TransactionType.EXPENSE) },
            label = { Text("Expense") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ExpenseColor.copy(alpha = 0.2f)
            )
        )
        FilterChip(
            selected = uiState.selectedType == TransactionType.TRANSFER,
            onClick = { onSelectedType(TransactionType.TRANSFER) },
            label = { Text("Transfer") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = TransferColor.copy(alpha = 0.2f)
            )
        )
        if (uiState.selectedType != null || uiState.searchQuery.isNotBlank()) {
            FilterChip(
                selected = false,
                onClick = onClear,
                label = { Text("Clear") },
                leadingIcon = {
                    Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            )
        }
    }
}

@Composable
private fun DailyContent(
    uiState: TransactionListUiState,
    onNavigateToEditTransaction: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(uiState.dailyGroups, key = { it.date }) { group ->
            DayGroupCard(group, onNavigateToEditTransaction)
        }
        emptyState(uiState, "Belum ada transaksi pada periode ini")
    }
}

@Composable
private fun NoteContent(
    uiState: TransactionListUiState,
    onNavigateToEditTransaction: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(uiState.noteGroups, key = { it.date }) { group ->
            DayGroupCard(group, onNavigateToEditTransaction)
        }
        if (uiState.noteGroups.isEmpty() && !uiState.isLoading) {
            item {
                EmptyMessage("Belum ada transaksi dengan catatan")
            }
        }
    }
}

@Composable
private fun DayGroupCard(
    group: TransactionDayGroup,
    onNavigateToEditTransaction: (Long) -> Unit,
) {
    CalmCard(modifier = Modifier.fillMaxWidth()) {
        SummaryHeader(
            title = DateUtils.formatDate(group.date),
            income = group.income,
            expense = group.expense,
            balance = group.balance,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            group.transactions.forEach { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { onNavigateToEditTransaction(transaction.id) }
                )
            }
        }
    }
}

@Composable
private fun CalendarContent(uiState: TransactionListUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            CalendarGrid(uiState.calendarCells)
        }
    }
}

@Composable
private fun CalendarGrid(cells: List<TransactionCalendarCell>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        cells.chunked(7).forEach { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                week.forEach { cell ->
                    CalendarCell(cell, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CalendarCell(
    cell: TransactionCalendarCell,
    modifier: Modifier = Modifier,
) {
    CalmCard(
        modifier = modifier.height(92.dp),
        contentPadding = PaddingValues(6.dp)
    ) {
        Text(
            text = cell.dayOfMonth.toString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (cell.isInCurrentMonth) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
            }
        )
        if (cell.income != 0.0 || cell.expense != 0.0 || cell.balance != 0.0) {
            Spacer(modifier = Modifier.height(4.dp))
            CalendarAmount("I", cell.income, IncomeColor)
            CalendarAmount("E", cell.expense, ExpenseColor)
            CalendarAmount("B", cell.balance, balanceColor(cell.balance))
        }
    }
}

@Composable
private fun CalendarAmount(label: String, amount: Double, color: Color) {
    if (amount == 0.0) return
    Text(
        text = "$label ${CurrencyFormatter.formatIDRCompact(kotlin.math.abs(amount))}",
        style = MaterialTheme.typography.labelSmall,
        color = color,
        maxLines = 1
    )
}

@Composable
private fun MonthlyContent(uiState: TransactionListUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(uiState.monthlySummaries, key = { "${it.year}-${it.month}" }) { summary ->
            MonthSummaryRow(summary)
        }
    }
}

@Composable
private fun MonthSummaryRow(summary: TransactionMonthSummary) {
    FinanceListRow(
        icon = Icons.Default.SwapHoriz,
        iconTint = balanceColor(summary.balance),
        title = monthLabel(summary.year, summary.month),
        subtitle = "Income ${CurrencyFormatter.formatIDR(summary.income)} | Expense ${CurrencyFormatter.formatIDR(summary.expense)}",
        trailing = {
            Text(
                text = CurrencyFormatter.formatIDR(summary.balance),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = balanceColor(summary.balance)
            )
        },
        contentDescription = "Monthly summary"
    )
}

@Composable
private fun TotalContent(uiState: TransactionListUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            TotalSummaryCard(uiState.totalSummary)
        }
    }
}

@Composable
private fun TotalSummaryCard(summary: TransactionTotalSummary) {
    CalmCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Total Summary",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        SummaryLine("Income", summary.income, IncomeColor)
        SummaryLine("Expense", summary.expense, ExpenseColor)
        SummaryLine("Balance", summary.balance, balanceColor(summary.balance))
        SummaryLine("Transactions", summary.transactionCount.toDouble(), MaterialTheme.colorScheme.onSurface, isCount = true)
    }
}

@Composable
private fun SummaryHeader(
    title: String,
    income: Double,
    expense: Double,
    balance: Double,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Income ${CurrencyFormatter.formatIDR(income)} | Expense ${CurrencyFormatter.formatIDR(expense)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Balance ${CurrencyFormatter.formatIDR(balance)}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = balanceColor(balance)
        )
    }
}

@Composable
private fun SummaryLine(
    label: String,
    value: Double,
    color: Color,
    isCount: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = if (isCount) value.toInt().toString() else CurrencyFormatter.formatIDR(value),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val typeColor = when (transaction.type) {
        TransactionType.INCOME -> IncomeColor
        TransactionType.EXPENSE -> ExpenseColor
        TransactionType.TRANSFER -> TransferColor
    }
    val typeIcon = when (transaction.type) {
        TransactionType.INCOME -> Icons.Default.ArrowDownward
        TransactionType.EXPENSE -> Icons.Default.ArrowUpward
        TransactionType.TRANSFER -> Icons.Default.SwapHoriz
    }

    FinanceListRow(
        icon = typeIcon,
        iconTint = typeColor,
        title = transaction.note.ifEmpty { transaction.type.displayName },
        subtitle = DateUtils.getRelativeDateLabel(transaction.date),
        trailing = {
            Text(
                text = signedTransactionAmount(transaction.type, transaction.amount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = transactionAmountColor(transaction.type)
            )
        },
        onClick = onClick,
        contentDescription = transaction.type.displayName
    )
}

private fun androidx.compose.foundation.lazy.LazyListScope.emptyState(
    uiState: TransactionListUiState,
    emptyText: String,
) {
    if (uiState.transactions.isEmpty() && !uiState.isLoading) {
        item {
            EmptyMessage(
                text = if (uiState.searchQuery.isNotBlank() || uiState.selectedType != null) {
                    "Tidak ada transaksi yang sesuai filter"
                } else {
                    emptyText
                }
            )
        }
    }
}

@Composable
private fun EmptyMessage(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun balanceColor(balance: Double): Color {
    return when {
        balance > 0.0 -> IncomeColor
        balance < 0.0 -> ExpenseColor
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

private fun periodLabel(mode: ExpenseViewMode, anchorDate: Long): String {
    return when (mode) {
        ExpenseViewMode.DAILY,
        ExpenseViewMode.CALENDAR,
        ExpenseViewMode.NOTE -> DateUtils.formatMonthYear(anchorDate)
        ExpenseViewMode.MONTHLY,
        ExpenseViewMode.TOTAL -> Calendar.getInstance().apply {
            timeInMillis = anchorDate
        }.get(Calendar.YEAR).toString()
    }
}

private fun rangeHint(mode: ExpenseViewMode): String {
    return when (mode) {
        ExpenseViewMode.DAILY -> "Daily groups"
        ExpenseViewMode.CALENDAR -> "Calendar"
        ExpenseViewMode.MONTHLY -> "Monthly summaries"
        ExpenseViewMode.TOTAL -> "Year total"
        ExpenseViewMode.NOTE -> "Notes"
    }
}

private fun monthLabel(year: Int, month: Int): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(calendar.timeInMillis))
}
