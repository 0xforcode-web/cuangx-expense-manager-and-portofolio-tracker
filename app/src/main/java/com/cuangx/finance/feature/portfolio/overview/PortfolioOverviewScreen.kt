package com.cuangx.finance.feature.portfolio.overview

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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.cuangx.finance.core.ui.theme.LossColor
import com.cuangx.finance.core.ui.theme.ProfitColor
import com.cuangx.finance.core.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioOverviewScreen(
    onNavigateToAddJournal: () -> Unit,
    onNavigateToHoldingDetail: (String) -> Unit,
    viewModel: PortfolioOverviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Portfolio") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = viewModel::refreshPrices) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Prices")
                    }
                }
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            item {
                PortfolioSummaryCard(
                    totalValue = uiState.totalValue,
                    totalPnl = uiState.totalPnl,
                    totalPnlPercent = uiState.totalPnlPercent
                )
            }

            if (uiState.holdings.isEmpty() && !uiState.isLoading) {
                item {
                    Text(
                        text = "Belum ada posisi. Tambahkan entry di Journal.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            items(uiState.holdings, key = { it.ticker }) { holding ->
                HoldingItem(
                    holding = holding,
                    onClick = { onNavigateToHoldingDetail(holding.ticker) }
                )
            }
        }
    }
}

@Composable
private fun PortfolioSummaryCard(
    totalValue: Double,
    totalPnl: Double,
    totalPnlPercent: Double
) {
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
                text = "Total Portfolio Value",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = CurrencyFormatter.formatIDR(totalValue),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${if (totalPnl >= 0) "+" else ""}${CurrencyFormatter.formatIDR(totalPnl)} (${CurrencyFormatter.formatPercent(totalPnlPercent)})",
                style = MaterialTheme.typography.bodyMedium,
                color = if (totalPnl >= 0) ProfitColor else LossColor
            )
        }
    }
}

@Composable
private fun HoldingItem(
    holding: HoldingPosition,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = holding.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${holding.quantity} × ${holding.currentPrice?.let { CurrencyFormatter.formatIDR(it) } ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Avg: ${CurrencyFormatter.formatIDR(holding.avgBuyPrice)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CurrencyFormatter.formatIDR(holding.currentValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${if (holding.pnl >= 0) "+" else ""}${CurrencyFormatter.formatIDR(holding.pnl)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (holding.pnl >= 0) ProfitColor else LossColor
                )
                Text(
                    text = CurrencyFormatter.formatPercent(holding.pnlPercent),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (holding.pnl >= 0) ProfitColor else LossColor
                )
            }
        }
    }
}
