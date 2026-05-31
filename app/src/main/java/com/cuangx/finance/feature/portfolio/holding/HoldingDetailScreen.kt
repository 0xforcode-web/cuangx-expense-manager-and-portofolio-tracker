package com.cuangx.finance.feature.portfolio.holding

import androidx.compose.foundation.layout.*
import java.util.Locale
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuangx.finance.core.ui.components.DeltaText
import com.cuangx.finance.core.ui.components.FinanceListRow
import com.cuangx.finance.core.ui.components.HeroCard
import com.cuangx.finance.core.ui.components.MoneyText
import com.cuangx.finance.core.ui.theme.ExpenseColor
import com.cuangx.finance.core.ui.theme.IncomeColor
import com.cuangx.finance.core.util.CurrencyFormatter
import com.cuangx.finance.core.util.DateUtils
import com.cuangx.finance.domain.model.JournalAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoldingDetailScreen(
    ticker: String,
    onNavigateBack: () -> Unit,
    viewModel: HoldingDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(ticker) {
        viewModel.loadHolding(ticker)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(uiState.error!!)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    HeroCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = uiState.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.assetType,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Qty", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                                Text(
                                    text = String.format(Locale.getDefault(), "%.2f", uiState.currentQty),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Avg Buy", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                                Text(
                                    text = CurrencyFormatter.formatIDR(uiState.avgBuyPrice),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Current", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                                Text(
                                    text = CurrencyFormatter.formatIDR(uiState.priceData?.price ?: uiState.avgBuyPrice),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(16.dp))

                        MoneyText(
                            amount = uiState.currentValue,
                            emphasized = true,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        DeltaText(amount = uiState.pnl, percent = uiState.pnlPercent)
                    }
                }

                item {
                    Text(
                        text = "Riwayat Transaksi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }

                items(uiState.entries) { entry ->
                    val actionColor = when (entry.action) {
                        JournalAction.BUY -> ExpenseColor
                        JournalAction.SELL -> IncomeColor
                        JournalAction.DIVIDEND -> IncomeColor
                    }
                    val actionIcon = when (entry.action) {
                        JournalAction.BUY -> Icons.Default.ArrowUpward
                        JournalAction.SELL -> Icons.Default.ArrowDownward
                        JournalAction.DIVIDEND -> Icons.Default.AttachMoney
                    }

                    FinanceListRow(
                        icon = actionIcon,
                        iconTint = actionColor,
                        title = "${entry.action.displayName} ${entry.quantity} @ ${CurrencyFormatter.formatIDR(entry.price)}",
                        subtitle = DateUtils.formatDate(entry.date),
                        supporting = entry.reason.ifBlank { null },
                        trailing = {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = CurrencyFormatter.formatIDR(entry.quantity * entry.price),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (entry.fee > 0) {
                                    Text(
                                        text = "Fee: ${CurrencyFormatter.formatIDR(entry.fee)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
