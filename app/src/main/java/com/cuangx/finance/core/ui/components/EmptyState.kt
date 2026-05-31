package com.cuangx.finance.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cuangx.finance.core.ui.theme.CuangXSpacing

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(CuangXSpacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onAction) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun EmptyTransactions(onAddClick: () -> Unit) {
    EmptyState(
        icon = Icons.Outlined.Receipt,
        title = "Belum ada transaksi",
        message = "Mulai catat pemasukan dan pengeluaran kamu",
        actionText = "Tambah Transaksi",
        onAction = onAddClick
    )
}

@Composable
fun EmptyPortfolio(onAddClick: () -> Unit) {
    EmptyState(
        icon = Icons.Outlined.ShowChart,
        title = "Belum ada investasi",
        message = "Mulai catat investasi kamu di Journal",
        actionText = "Tambah Journal",
        onAction = onAddClick
    )
}

@Composable
fun EmptyDebts(onAddClick: () -> Unit) {
    EmptyState(
        icon = Icons.Outlined.CreditCard,
        title = "Belum ada utang/piutang",
        message = "Catat utang dan piutang kamu di sini",
        actionText = "Tambah",
        onAction = onAddClick
    )
}

@Composable
fun EmptyAccounts(onAddClick: () -> Unit) {
    EmptyState(
        icon = Icons.Outlined.AccountBalanceWallet,
        title = "Belum ada akun",
        message = "Tambahkan akun bank, cash, atau e-wallet kamu",
        actionText = "Tambah Akun",
        onAction = onAddClick
    )
}

@Composable
fun EmptyJournal(onAddClick: () -> Unit) {
    EmptyState(
        icon = Icons.Outlined.Book,
        title = "Belum ada journal",
        message = "Catat keputusan investasi kamu di sini",
        actionText = "Tambah Journal",
        onAction = onAddClick
    )
}

@Composable
fun EmptyStatistics() {
    EmptyState(
        icon = Icons.Outlined.BarChart,
        title = "Belum ada data",
        message = "Mulai catat transaksi untuk melihat statistik"
    )
}
