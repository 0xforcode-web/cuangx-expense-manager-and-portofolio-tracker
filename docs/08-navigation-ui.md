# CuangX Finance - Navigation & UI

## Navigation Structure

### Bottom Navigation Bar

```
┌─────────────┬─────────────┬─────────────┬─────────────┐
│  Dashboard   │  Expense    │  Portfolio  │    More     │
│  ic_home     │  ic_wallet  │  ic_chart   │  ic_menu    │
└─────────────┴─────────────┴─────────────┴─────────────┘
```

| Tab | Route | Description |
|---|---|---|
| Dashboard | `dashboard` | Unified overview: net worth, cashflow, portfolio |
| Expense | `transactions` | Transaction list with filters |
| Portfolio | `portfolio` | Portfolio overview with holdings |
| More | (opens submenu) | Accounts, Categories, Budget, Stats, Recurring, Debts, Settings |

### More Tab Submenu

```
┌────────────────────────────────────┐
║  MENU                              │
├────────────────────────────────────┤
║  💳 Accounts                       │
║  🏷️ Categories                     │
║  📊 Budget                         │
║  📈 Statistics                     │
║  🔄 Recurring                      │
║  💸 Utang & Piutang                │
║  ⚙️ Settings                       │
└────────────────────────────────────┘
```

### Route Definitions (Sealed Class)

```kotlin
sealed class Screen(val route: String) {
    // Main tabs
    data object Dashboard : Screen("dashboard")
    data object TransactionList : Screen("transactions")
    data object PortfolioOverview : Screen("portfolio")

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

    // Portfolio
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
```

### Navigation Graph

```kotlin
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {

        // Dashboard
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToTransactions = { navController.navigate(Screen.TransactionList.route) },
                onNavigateToPortfolio = { navController.navigate(Screen.PortfolioOverview.route) },
                onNavigateToDebts = { navController.navigate(Screen.DebtList.route) }
            )
        }

        // Transactions
        composable(Screen.TransactionList.route) {
            TransactionListScreen(
                onAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                onEditTransaction = { id -> navController.navigate(Screen.EditTransaction.createRoute(id)) }
            )
        }
        composable(Screen.AddTransaction.route) {
            AddEditTransactionScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.EditTransaction.route) {
            AddEditTransactionScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Accounts
        composable(Screen.AccountList.route) {
            AccountListScreen(
                onAddAccount = { navController.navigate(Screen.AddAccount.route) },
                onAccountDetail = { id -> navController.navigate(Screen.AccountDetail.createRoute(id)) }
            )
        }
        composable(Screen.AccountDetail.route) {
            AccountDetailScreen(
                onEditAccount = { id -> navController.navigate(Screen.EditAccount.createRoute(id)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Portfolio
        composable(Screen.PortfolioOverview.route) {
            PortfolioOverviewScreen(
                onAddHolding = { navController.navigate(Screen.AddHolding.route) },
                onHoldingDetail = { id -> navController.navigate(Screen.HoldingDetail.createRoute(id)) },
                onNavigateToAnalysis = { navController.navigate(Screen.Analysis.route) },
                onNavigateToNetWorth = { navController.navigate(Screen.NetWorth.route) },
                onNavigateToDividends = { navController.navigate(Screen.Dividend.route) }
            )
        }
        composable(Screen.HoldingDetail.route) {
            HoldingDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onEditHolding = { id -> navController.navigate(Screen.EditHolding.createRoute(id)) }
            )
        }

        // Debts
        composable(Screen.DebtList.route) {
            DebtListScreen(
                onAddDebt = { navController.navigate(Screen.AddDebt.route) },
                onDebtDetail = { id -> navController.navigate(Screen.DebtDetail.createRoute(id)) }
            )
        }
        composable(Screen.DebtDetail.route) {
            DebtDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onEditDebt = { id -> navController.navigate(Screen.EditDebt.createRoute(id)) }
            )
        }

        // Settings
        composable(Screen.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
```

## Theme (Material 3)

### Color Scheme

```kotlin
// Light theme
val LightPrimary = Color(0xFF1B5E20)        // Dark Green
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFA5D6A7)
val LightSecondary = Color(0xFF00796B)      // Teal
val LightBackground = Color(0xFFF5F5F5)
val LightSurface = Color(0xFFFFFFFF)
val LightError = Color(0xFFD32F2F)

// Dark theme
val DarkPrimary = Color(0xFF81C784)         // Light Green
val DarkOnPrimary = Color(0xFF000000)
val DarkPrimaryContainer = Color(0xFF2E7D32)
val DarkSecondary = Color(0xFF4DB6AC)
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkError = Color(0xFFEF5350)

// Semantic colors
val IncomeColor = Color(0xFF4CAF50)         // Green
val ExpenseColor = Color(0xFFF44336)        // Red
val TransferColor = Color(0xFF2196F3)       // Blue
val DebtColor = Color(0xFFFF9800)           // Orange
val ReceivableColor = Color(0xFF4CAF50)     // Green
val ProfitColor = Color(0xFF4CAF50)         // Green
val LossColor = Color(0xFFF44336)           // Red
```

### Typography

```kotlin
val CuangXTypography = Typography(
    displayLarge = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
    headlineLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 16.sp),
    bodyMedium = TextStyle(fontSize = 14.sp),
    bodySmall = TextStyle(fontSize = 12.sp),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    labelMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontSize = 10.sp)
)
```

## Shared UI Components

### AccountPicker
```kotlin
@Composable
fun AccountPicker(
    selectedAccountId: Long?,
    onAccountSelected: (Long) -> Unit,
    accounts: List<Account>,
    label: String = "Account"
)
```

### CategoryPicker
```kotlin
@Composable
fun CategoryPicker(
    selectedCategoryId: Long?,
    onCategorySelected: (Long) -> Unit,
    categories: List<Category>,
    type: TransactionType
)
```

### AmountInput
```kotlin
@Composable
fun AmountInput(
    amount: String,
    onAmountChange: (String) -> Unit,
    currency: String = "IDR"
)
```

### DatePickerField
```kotlin
@Composable
fun DatePickerField(
    date: Long?,
    onDateSelected: (Long) -> Unit,
    label: String = "Date"
)
```

### IconPicker
```kotlin
@Composable
fun IconPicker(
    selectedIcon: String,
    onIconSelected: (String) -> Unit
)
```

### EmptyState
```kotlin
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
)
```

## Screen Flow Diagram

```
App Launch
    │
    ├──→ Dashboard
    │       ├──→ TransactionList (tap "See All")
    │       ├──→ PortfolioOverview (tap "See All")
    │       ├──→ DebtList (tap "See All")
    │       └──→ AccountDetail (tap account)
    │
    ├──→ Expense Tab
    │       ├──→ TransactionList
    │       │       ├──→ AddTransaction
    │       │       └──→ EditTransaction
    │       └──→ (via More menu)
    │               ├──→ AccountList → AddAccount / AccountDetail
    │               ├──→ CategoryList → AddCategory
    │               ├──→ Budget → AddBudget
    │               ├──→ Statistics
    │               └──→ Recurring → AddRecurring
    │
    ├──→ Portfolio Tab
    │       ├──→ PortfolioOverview
    │       │       ├──→ AddHolding
    │       │       └──→ HoldingDetail → EditHolding
    │       ├──→ Analysis
    │       ├──→ Dividend
    │       └──→ NetWorth
    │
    └──→ More Tab
            ├──→ AccountList
            ├──→ CategoryList
            ├──→ Budget
            ├──→ Statistics
            ├──→ Recurring
            ├──→ DebtList → AddDebt / DebtDetail
            └──→ Settings
```
