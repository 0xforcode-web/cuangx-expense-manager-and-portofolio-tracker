package com.cuangx.finance.feature.portfolio.networth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuangx.finance.core.ui.components.HeroCard
import com.cuangx.finance.core.ui.components.MoneyText
import com.cuangx.finance.core.ui.theme.DebtColor
import com.cuangx.finance.core.ui.theme.IncomeColor
import com.cuangx.finance.core.ui.theme.TransferColor
import com.cuangx.finance.core.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetWorthScreen(
    onNavigateBack: () -> Unit,
    viewModel: NetWorthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kekayaan Bersih") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            uiState.summary?.let { summary ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        HeroCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Total Kekayaan Bersih",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            MoneyText(
                                amount = summary.totalNetWorth,
                                emphasized = true,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    item {
                        Text(
                            "Rincian Aset & Liabilitas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    item {
                        NetWorthItem(
                            label = "Aset Lancar (Kas/Bank)",
                            amount = summary.liquidAssets,
                            icon = Icons.Default.AccountBalance,
                            color = TransferColor
                        )
                    }

                    item {
                        NetWorthItem(
                            label = "Investasi (Portofolio)",
                            amount = summary.investments,
                            icon = Icons.AutoMirrored.Filled.ShowChart,
                            color = IncomeColor
                        )
                    }

                    item {
                        NetWorthItem(
                            label = "Piutang",
                            amount = summary.receivables,
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            color = IncomeColor
                        )
                    }

                    item {
                        NetWorthItem(
                            label = "Utang",
                            amount = -summary.debts,
                            icon = Icons.AutoMirrored.Filled.TrendingDown,
                            color = DebtColor
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Tentang Net Worth",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Kekayaan bersih adalah total nilai aset yang kamu miliki dikurangi dengan seluruh kewajiban utang. Ini adalah indikator kesehatan finansial jangka panjang.",
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

@Composable
fun NetWorthItem(
    label: String,
    amount: Double,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.bodyMedium)
                Text(
                    CurrencyFormatter.formatIDR(amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (amount < 0) DebtColor else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
