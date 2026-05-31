package com.cuangx.finance.core.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cuangx.finance.feature.expense.account.AccountListScreen
import com.cuangx.finance.feature.expense.account.AddEditAccountScreen
import com.cuangx.finance.feature.expense.budget.AddEditBudgetScreen
import com.cuangx.finance.feature.expense.budget.BudgetScreen
import com.cuangx.finance.feature.expense.category.AddEditCategoryScreen
import com.cuangx.finance.feature.expense.category.CategoryListScreen
import com.cuangx.finance.feature.expense.recurring.AddEditRecurringScreen
import com.cuangx.finance.feature.expense.recurring.RecurringScreen
import com.cuangx.finance.feature.expense.statistics.StatisticsScreen
import com.cuangx.finance.feature.expense.transaction.AddEditTransactionScreen
import com.cuangx.finance.feature.expense.transaction.TransactionListScreen
import com.cuangx.finance.feature.dashboard.DashboardScreen
import com.cuangx.finance.feature.debt.AddEditDebtScreen
import com.cuangx.finance.feature.debt.DebtDetailScreen
import com.cuangx.finance.feature.debt.DebtListScreen
import com.cuangx.finance.feature.portfolio.analysis.AnalysisScreen
import com.cuangx.finance.feature.portfolio.dividend.DividendScreen
import com.cuangx.finance.feature.portfolio.holding.HoldingDetailScreen
import com.cuangx.finance.feature.portfolio.journal.AddEditJournalScreen
import com.cuangx.finance.feature.portfolio.journal.JournalListScreen
import com.cuangx.finance.feature.portfolio.networth.NetWorthScreen
import com.cuangx.finance.feature.portfolio.overview.PortfolioOverviewScreen
import com.cuangx.finance.feature.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        // Dashboard
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToTransactions = {
                    navController.navigate(Screen.TransactionList.route)
                },
                onNavigateToPortfolio = {
                    navController.navigate(Screen.PortfolioOverview.route)
                },
                onNavigateToDebts = {
                    navController.navigate(Screen.DebtList.route)
                }
            )
        }

        // Transactions
        composable(Screen.TransactionList.route) {
            TransactionListScreen(
                onNavigateToAddTransaction = {
                    navController.navigate(Screen.AddTransaction.route)
                },
                onNavigateToEditTransaction = { id ->
                    navController.navigate(Screen.EditTransaction.createRoute(id))
                }
            )
        }
        composable(Screen.AddTransaction.route) {
            AddEditTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.EditTransaction.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId")
            AddEditTransactionScreen(
                transactionId = transactionId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Journal
        composable(Screen.JournalList.route) {
            JournalListScreen(
                onNavigateToAddJournal = {
                    navController.navigate(Screen.AddHolding.route)
                },
                onNavigateToEditJournal = { id ->
                    navController.navigate(Screen.EditHolding.createRoute(id))
                }
            )
        }

        // Portfolio
        composable(Screen.PortfolioOverview.route) {
            PortfolioOverviewScreen(
                onNavigateToAddJournal = {
                    navController.navigate(Screen.AddHolding.route)
                },
                onNavigateToHoldingDetail = { ticker ->
                    navController.navigate("portfolio/holding/detail/$ticker")
                }
            )
        }
        composable(Screen.AddHolding.route) {
            AddEditJournalScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.EditHolding.route,
            arguments = listOf(navArgument("holdingId") { type = NavType.LongType })
        ) { backStackEntry ->
            val journalId = backStackEntry.arguments?.getLong("holdingId") ?: 0
            AddEditJournalScreen(
                journalId = journalId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "portfolio/holding/detail/{ticker}",
            arguments = listOf(navArgument("ticker") { type = NavType.StringType })
        ) { backStackEntry ->
            val ticker = backStackEntry.arguments?.getString("ticker") ?: ""
            HoldingDetailScreenPlaceholder(ticker = ticker, onNavigateBack = { navController.popBackStack() })
        }

        // Accounts
        composable(Screen.AccountList.route) {
            AccountListScreen(
                onNavigateToAddAccount = {
                    navController.navigate(Screen.AddAccount.route)
                },
                onNavigateToAccountDetail = { id ->
                    navController.navigate(Screen.AccountDetail.createRoute(id))
                }
            )
        }
        composable(Screen.AddAccount.route) {
            AddEditAccountScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.EditAccount.route,
            arguments = listOf(navArgument("accountId") { type = NavType.LongType })
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getLong("accountId")
            AddEditAccountScreen(
                accountId = accountId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AccountDetail.route) {
            PlaceholderScreen("Account Detail")
        }

        // Categories
        composable(Screen.CategoryList.route) {
            CategoryListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddCategory = {
                    navController.navigate(Screen.AddCategory.route)
                }
            )
        }
        composable(Screen.AddCategory.route) {
            AddEditCategoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // More
        composable("more") {
            MoreScreen(
                onNavigateToAccounts = { navController.navigate(Screen.AccountList.route) },
                onNavigateToCategories = { navController.navigate(Screen.CategoryList.route) },
                onNavigateToBudget = { navController.navigate(Screen.Budget.route) },
                onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) },
                onNavigateToRecurring = { navController.navigate(Screen.Recurring.route) },
                onNavigateToDebts = { navController.navigate(Screen.DebtList.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        // Budget
        composable(Screen.Budget.route) {
            BudgetScreen(
                onNavigateToAddBudget = {
                    navController.navigate(Screen.AddBudget.route)
                }
            )
        }
        composable(Screen.AddBudget.route) {
            AddEditBudgetScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.EditBudget.route,
            arguments = listOf(navArgument("budgetId") { type = NavType.LongType })
        ) { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getLong("budgetId")
            AddEditBudgetScreen(
                budgetId = budgetId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Statistics
        composable(Screen.Statistics.route) {
            StatisticsScreen()
        }

        // Recurring
        composable(Screen.Recurring.route) {
            RecurringScreen(
                onNavigateToAddRecurring = {
                    navController.navigate(Screen.AddRecurring.route)
                }
            )
        }
        composable(Screen.AddRecurring.route) {
            AddEditRecurringScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.EditRecurring.route,
            arguments = listOf(navArgument("recurringId") { type = NavType.LongType })
        ) { backStackEntry ->
            val recurringId = backStackEntry.arguments?.getLong("recurringId")
            AddEditRecurringScreen(
                recurringId = recurringId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Dividends
        composable(Screen.Dividend.route) {
            DividendScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Analysis
        composable(Screen.Analysis.route) {
            AnalysisScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Net Worth
        composable(Screen.NetWorth.route) {
            NetWorthScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Debts
        composable(Screen.DebtList.route) {
            DebtListScreen(
                onNavigateToAddDebt = {
                    navController.navigate(Screen.AddDebt.route)
                },
                onNavigateToDebtDetail = { id ->
                    navController.navigate(Screen.DebtDetail.createRoute(id))
                }
            )
        }
        composable(Screen.AddDebt.route) {
            AddEditDebtScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.EditDebt.route,
            arguments = listOf(navArgument("debtId") { type = NavType.LongType })
        ) { backStackEntry ->
            val debtId = backStackEntry.arguments?.getLong("debtId") ?: 0
            AddEditDebtScreen(
                debtId = debtId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.DebtDetail.route,
            arguments = listOf(navArgument("debtId") { type = NavType.LongType })
        ) { backStackEntry ->
            val debtId = backStackEntry.arguments?.getLong("debtId") ?: 0
            DebtDetailScreen(
                debtId = debtId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.EditDebt.createRoute(id))
                }
            )
        }

        // Settings
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun HoldingDetailScreenPlaceholder(ticker: String, onNavigateBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Holding Detail: $ticker",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
