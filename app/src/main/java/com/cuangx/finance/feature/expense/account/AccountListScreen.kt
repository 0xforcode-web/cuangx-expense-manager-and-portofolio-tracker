package com.cuangx.finance.feature.expense.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuangx.finance.core.ui.components.CalmCard
import com.cuangx.finance.core.ui.components.EmptyAccounts
import com.cuangx.finance.core.ui.components.FinanceListRow
import com.cuangx.finance.core.ui.theme.CuangXSpacing
import com.cuangx.finance.core.util.CurrencyFormatter
import com.cuangx.finance.domain.model.Account
import com.cuangx.finance.domain.model.AccountType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountListScreen(
    onNavigateToAddAccount: () -> Unit,
    onNavigateToAccountDetail: (Long) -> Unit,
    viewModel: AccountListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accounts") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddAccount,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Account")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TotalBalanceCard(totalBalance = uiState.totalBalance)

            if (uiState.accounts.isEmpty()) {
                EmptyAccounts(onAddClick = onNavigateToAddAccount)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(CuangXSpacing.xs),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(CuangXSpacing.md)
                ) {
                    items(uiState.accounts, key = { it.id }) { account ->
                        AccountItem(
                            account = account,
                            onClick = { onNavigateToAccountDetail(account.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalBalanceCard(totalBalance: Double) {
    CalmCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(CuangXSpacing.md)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(CuangXSpacing.xs))
            Text(
                text = CurrencyFormatter.formatIDR(totalBalance),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AccountItem(
    account: Account,
    onClick: () -> Unit
) {
    FinanceListRow(
        icon = getAccountIcon(account.type),
        iconTint = Color(account.color),
        title = account.name,
        subtitle = "${account.type.displayName} • ${account.currency}",
        trailing = {
            Text(
                text = CurrencyFormatter.formatIDR(account.balance),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (account.balance >= 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        },
        onClick = onClick
    )
}

private fun getAccountIcon(type: AccountType): ImageVector {
    return when (type) {
        AccountType.CASH -> Icons.Default.Payments
        AccountType.BANK -> Icons.Default.AccountBalance
        AccountType.CREDIT_CARD -> Icons.Default.CreditCard
        AccountType.E_WALLET -> Icons.Default.Wallet
        AccountType.INVESTMENT -> Icons.Default.TrendingUp
        AccountType.BROKER -> Icons.Default.TrendingUp
    }
}
