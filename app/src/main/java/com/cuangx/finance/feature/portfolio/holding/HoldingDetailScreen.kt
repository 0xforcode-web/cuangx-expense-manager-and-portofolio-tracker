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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.cuangx.finance.core.ui.theme.ExpenseColor
import com.cuangx.finance.core.ui.theme.IncomeColor
import com.cuangx.finance.core.ui.theme.LossColor
import com.cuangx.finance.core.ui.theme.ProfitColor
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = entries.firstOrNull()?.name ?: ticker,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = entries.firstOrNull()?.assetType?.displayName ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Qty", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = currentQty.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Avg Buy", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = CurrencyFormatter.formatIDR(avgBuyPrice),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Current", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = CurrencyFormatter.formatIDR(currentPrice),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = CurrencyFormatter.formatIDR(currentValue),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${if (pnl >= 0) "+" else ""}${CurrencyFormatter.formatIDR(pnl)} (${CurrencyFormatter.formatPercent(pnlPercent)})",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (pnl >= 0) ProfitColor else LossColor
                        )
                    }
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${entry.action.displayName} ${entry.quantity} @ ${CurrencyFormatter.formatIDR(entry.price)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = when (entry.action) {
                                    JournalAction.BUY -> ExpenseColor
                                    JournalAction.SELL -> IncomeColor
                                    JournalAction.DIVIDEND -> IncomeColor
                                }
                            )
                            Text(
                                text = DateUtils.formatDate(entry.date),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (entry.reason.isNotBlank()) {
                                Text(
                                    text = entry.reason,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
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
                }
            }
        }
    }
}
