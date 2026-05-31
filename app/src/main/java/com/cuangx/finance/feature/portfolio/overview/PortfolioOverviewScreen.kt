package com.cuangx.finance.feature.portfolio.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuangx.finance.core.ui.theme.CategoryColors
import com.cuangx.finance.core.ui.theme.LossColor
import com.cuangx.finance.core.ui.theme.ProfitColor
import com.cuangx.finance.core.util.CurrencyFormatter
import com.cuangx.finance.domain.model.AssetType

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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            // Portfolio Summary
            item {
                PortfolioSummaryCard(
                    totalValue = uiState.totalValue,
                    totalCost = uiState.totalCost,
                    totalPnl = uiState.totalPnl,
                    totalPnlPercent = uiState.totalPnlPercent
                )
            }

            // Asset Allocation Chart
            item {
                AssetAllocationChart(holdings = uiState.holdings)
            }

            // Holdings List Header
            item {
                Text(
                    text = "Holdings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
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

            // Holdings List
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
    totalCost: Double,
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Invested",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = CurrencyFormatter.formatIDR(totalCost),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "P&L",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${if (totalPnl >= 0) "+" else ""}${CurrencyFormatter.formatIDR(totalPnl)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (totalPnl >= 0) ProfitColor else LossColor
                    )
                    Text(
                        text = CurrencyFormatter.formatPercent(totalPnlPercent),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (totalPnl >= 0) ProfitColor else LossColor
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetAllocationChart(holdings: List<HoldingPosition>) {
    if (holdings.isEmpty()) return

    // Group by asset type
    val allocations = holdings.groupBy { it.assetType }
        .map { (type, typeHoldings) ->
            Triple(type, typeHoldings.sumOf { it.currentValue }, typeHoldings.size)
        }
        .sortedByDescending { it.second }

    val totalValue = allocations.sumOf { it.second }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Asset Allocation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Stacked bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    allocations.forEachIndexed { index, (type, value, _) ->
                        val fraction = (value / totalValue).toFloat()
                        val color = getCategoryColor(index)
                        Box(
                            modifier = Modifier
                                .weight(fraction)
                                .height(12.dp)
                                .background(color)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend
            allocations.forEachIndexed { index, (type, value, count) ->
                val percentage = if (totalValue > 0) (value / totalValue * 100) else 0.0
                val color = getCategoryColor(index)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = type.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${String.format("%.0f", percentage)}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(40.dp)
                    )
                    Text(
                        text = CurrencyFormatter.formatIDR(value),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun getCategoryColor(index: Int): Color {
    return CategoryColors[index % CategoryColors.size]
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
                    text = "${holding.assetType.displayName} • ${holding.quantity}",
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
