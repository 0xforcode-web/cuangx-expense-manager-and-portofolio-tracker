package com.cuangx.finance.core.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.cuangx.finance.core.ui.components.CalmCard
import com.cuangx.finance.core.ui.components.SectionHeader
import com.cuangx.finance.core.ui.theme.CuangXSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onNavigateToAccounts: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToRecurring: () -> Unit,
    onNavigateToDebts: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Menu") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(CuangXSpacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            SectionHeader(title = "Money Setup")
            Spacer(modifier = Modifier.height(CuangXSpacing.xs))
            CalmCard(modifier = Modifier.fillMaxWidth()) {
                MenuItem(
                    icon = Icons.Default.AccountBalance,
                    title = "Accounts",
                    subtitle = "Kelola akun bank, cash, e-wallet",
                    onClick = onNavigateToAccounts
                )
                MenuItem(
                    icon = Icons.Default.Category,
                    title = "Kategori",
                    subtitle = "Kelola kategori pemasukan & pengeluaran",
                    onClick = onNavigateToCategories
                )
            }

            Spacer(modifier = Modifier.height(CuangXSpacing.md))

            SectionHeader(title = "Planning")
            Spacer(modifier = Modifier.height(CuangXSpacing.xs))
            CalmCard(modifier = Modifier.fillMaxWidth()) {
                MenuItem(
                    icon = Icons.Default.PieChart,
                    title = "Budget",
                    subtitle = "Atur budget per kategori",
                    onClick = onNavigateToBudget
                )
                MenuItem(
                    icon = Icons.Default.BarChart,
                    title = "Statistik",
                    subtitle = "Lihat statistik keuangan",
                    onClick = onNavigateToStatistics
                )
                MenuItem(
                    icon = Icons.Default.Repeat,
                    title = "Recurring",
                    subtitle = "Transaksi berulang otomatis",
                    onClick = onNavigateToRecurring
                )
            }

            Spacer(modifier = Modifier.height(CuangXSpacing.md))

            SectionHeader(title = "Obligations")
            Spacer(modifier = Modifier.height(CuangXSpacing.xs))
            CalmCard(modifier = Modifier.fillMaxWidth()) {
                MenuItem(
                    icon = Icons.Default.CreditCard,
                    title = "Utang & Piutang",
                    subtitle = "Kelola utang dan piutang",
                    onClick = onNavigateToDebts
                )
            }

            Spacer(modifier = Modifier.height(CuangXSpacing.md))

            SectionHeader(title = "App")
            Spacer(modifier = Modifier.height(CuangXSpacing.xs))
            CalmCard(modifier = Modifier.fillMaxWidth()) {
                MenuItem(
                    icon = Icons.Default.Settings,
                    title = "Settings",
                    subtitle = "Pengaturan aplikasi",
                    onClick = onNavigateToSettings
                )
            }
        }
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = CuangXSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(CuangXSpacing.sm))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
