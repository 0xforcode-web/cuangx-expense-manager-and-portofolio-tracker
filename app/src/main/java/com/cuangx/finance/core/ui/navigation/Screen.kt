package com.cuangx.finance.core.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // Main tabs
    data object Dashboard : Screen("dashboard")
    data object TransactionList : Screen("transactions")
    data object JournalList : Screen("journal")
    data object PortfolioOverview : Screen("portfolio")
    data object More : Screen("more")

    // Expense
    data object AddTransaction : Screen("transactions/add")
    data object EditTransaction : Screen("transactions/edit/{transactionId}") {
        fun createRoute(transactionId: Long) = "transactions/edit/$transactionId"
    }
    data object AccountList : Screen("accounts")
    data object AddAccount : Screen("accounts/add")
    data object EditAccount : Screen("accounts/edit/{accountId}") {
        fun createRoute(accountId: Long) = "accounts/edit/$accountId"
    }
    data object AccountDetail : Screen("accounts/detail/{accountId}") {
        fun createRoute(accountId: Long) = "accounts/detail/$accountId"
    }
    data object CategoryList : Screen("categories")
    data object AddCategory : Screen("categories/add")
    data object EditCategory : Screen("categories/edit/{categoryId}") {
        fun createRoute(categoryId: Long) = "categories/edit/$categoryId"
    }
    data object Budget : Screen("budget")
    data object AddBudget : Screen("budget/add")
    data object EditBudget : Screen("budget/edit/{budgetId}") {
        fun createRoute(budgetId: Long) = "budget/edit/$budgetId"
    }
    data object Statistics : Screen("statistics")
    data object Recurring : Screen("recurring")
    data object AddRecurring : Screen("recurring/add")
    data object EditRecurring : Screen("recurring/edit/{recurringId}") {
        fun createRoute(recurringId: Long) = "recurring/edit/$recurringId"
    }

    // Portfolio / Journal
    data object AddHolding : Screen("portfolio/holding/add")
    data object EditHolding : Screen("portfolio/holding/edit/{holdingId}") {
        fun createRoute(holdingId: Long) = "portfolio/holding/edit/$holdingId"
    }
    data object HoldingDetail : Screen("portfolio/holding/detail/{holdingId}") {
        fun createRoute(holdingId: Long) = "portfolio/holding/detail/$holdingId"
    }
    data object Dividend : Screen("portfolio/dividend")
    data object Analysis : Screen("portfolio/analysis")
    data object NetWorth : Screen("portfolio/networth")

    // Debt
    data object DebtList : Screen("debts")
    data object AddDebt : Screen("debts/add")
    data object EditDebt : Screen("debts/edit/{debtId}") {
        fun createRoute(debtId: Long) = "debts/edit/$debtId"
    }
    data object DebtDetail : Screen("debts/detail/{debtId}") {
        fun createRoute(debtId: Long) = "debts/detail/$debtId"
    }

    // Settings
    data object Settings : Screen("settings")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        screen = Screen.Dashboard,
        label = "Dashboard",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        screen = Screen.TransactionList,
        label = "Expense",
        selectedIcon = Icons.Filled.Wallet,
        unselectedIcon = Icons.Outlined.Wallet
    ),
    BottomNavItem(
        screen = Screen.JournalList,
        label = "Journal",
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book
    ),
    BottomNavItem(
        screen = Screen.PortfolioOverview,
        label = "Portfolio",
        selectedIcon = Icons.Filled.ShowChart,
        unselectedIcon = Icons.Outlined.ShowChart
    ),
    BottomNavItem(
        screen = Screen.More,
        label = "More",
        selectedIcon = Icons.Filled.MoreHoriz,
        unselectedIcon = Icons.Outlined.MoreHoriz
    )
)
