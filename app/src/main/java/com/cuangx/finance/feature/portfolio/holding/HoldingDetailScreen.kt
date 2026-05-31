package com.cuangx.finance.feature.portfolio.holding

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cuangx.finance.core.ui.components.DeltaText
import com.cuangx.finance.core.ui.components.FinanceListRow
import com.cuangx.finance.core.ui.components.HeroCard
import com.cuangx.finance.core.ui.components.MoneyText
import com.cuangx.finance.core.ui.theme.ExpenseColor
import com.cuangx.finance.core.ui.theme.IncomeColor
import com.cuangx.finance.core.util.CurrencyFormatter
import com.cuangx.finance.core.util.DateUtils
import com.cuangx.finance.domain.model.JournalAction
import com.cuangx.finance.domain.model.JournalEntry
import com.cuangx.finance.domain.model.PriceData
import com.cuangx.finance.domain.repository.JournalRepository
import com.cuangx.finance.domain.repository.PriceRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoldingDetailScreen(
    ticker: String,
    onNavigateBack: () -> Unit,
    journalRepository: JournalRepository,
    priceRepository: PriceRepository
) {
    var entries by remember { mutableStateOf<List<JournalEntry>>(emptyList()) }
    var priceData by remember { mutableStateOf<PriceData?>(null) }
    var currentQty by remember { mutableStateOf(0.0) }
    var avgBuyPrice by remember { mutableStateOf(0.0) }

    LaunchedEffect(ticker) {
        entries = journalRepository.getByTickerOnce(ticker)
        priceData = priceRepository.getPrice(ticker)
        currentQty = journalRepository.getCurrentQuantity(ticker)

        val buys = entries.filter { it.action == JournalAction.BUY }
        val totalBuyQty = buys.sumOf { it.quantity }
        val totalBuyCost = buys.sumOf { it.quantity * it.price }
        avgBuyPrice = if (totalBuyQty > 0) totalBuyCost / totalBuyQty else 0.0
    }

    val currentPrice = priceData?.price ?: avgBuyPrice
    val currentValue = currentQty * currentPrice
    val totalCost = currentQty * avgBuyPrice
    val pnl = currentValue - totalCost
    val pnlPercent = if (totalCost > 0) (pnl / totalCost) * 100 else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(entries.firstOrNull()?.name ?: ticker) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            item {
                HeroCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = entries.firstOrNull()?.name ?: ticker,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = entries.firstOrNull()?.assetType?.displayName ?: "",
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
                                text = currentQty.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Avg Buy", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                            Text(
                                text = CurrencyFormatter.formatIDR(avgBuyPrice),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Current", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                            Text(
                                text = CurrencyFormatter.formatIDR(currentPrice),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))

                    MoneyText(
                        amount = currentValue,
                        emphasized = true,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    DeltaText(amount = pnl, percent = pnlPercent)
                }
            }

            item {
                Text(
                    text = "Riwayat Transaksi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(entries.sortedByDescending { it.date }) { entry ->
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
