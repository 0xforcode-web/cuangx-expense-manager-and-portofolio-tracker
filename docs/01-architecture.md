# CuangX Finance - Architecture

## Arsitektur: MVVM + Clean Architecture

```
┌─────────────────────────────────────────────────────┐
│                  PRESENTATION                        │
│   Screens (Compose) ← ViewModels ← Use Cases        │
├─────────────────────────────────────────────────────┤
│                    DOMAIN                            │
│   Models, Repository Interfaces, Use Cases           │
├─────────────────────────────────────────────────────┤
│                     DATA                             │
│   Room DB, Retrofit APIs, DataStore, Repo Impls      │
└─────────────────────────────────────────────────────┘
```

## Package Structure

```
com.cuangx.finance/
├── CuangXApp.kt                          # Application class (Hilt entry)
├── MainActivity.kt                       # Single activity
│
├── core/
│   ├── database/
│   │   ├── CuangXDatabase.kt            # Room database definition
│   │   ├── Converters.kt                # Type converters (Date, Enum)
│   │   ├── dao/                         # Data Access Objects
│   │   │   ├── AccountDao.kt
│   │   │   ├── CategoryDao.kt
│   │   │   ├── TransactionDao.kt
│   │   │   ├── BudgetDao.kt
│   │   │   ├── RecurringTransactionDao.kt
│   │   │   ├── HoldingDao.kt
│   │   │   ├── PortfolioTransactionDao.kt
│   │   │   ├── DividendRecordDao.kt
│   │   │   ├── PriceCacheDao.kt
│   │   │   ├── DebtReceivableDao.kt
│   │   │   └── DebtReceivablePaymentDao.kt
│   │   └── entity/                      # Room entities
│   │       ├── AccountEntity.kt
│   │       ├── CategoryEntity.kt
│   │       ├── TransactionEntity.kt
│   │       ├── BudgetEntity.kt
│   │       ├── RecurringTransactionEntity.kt
│   │       ├── HoldingEntity.kt
│   │       ├── PortfolioTransactionEntity.kt
│   │       ├── DividendRecordEntity.kt
│   │       ├── PriceCacheEntity.kt
│   │       ├── DebtReceivableEntity.kt
│   │       └── DebtReceivablePaymentEntity.kt
│   │
│   ├── network/
│   │   ├── RetrofitProvider.kt           # Retrofit singleton
│   │   ├── yahoo/
│   │   │   ├── YahooFinanceApi.kt        # Retrofit interface
│   │   │   └── YahooFinanceModels.kt     # Response DTOs
│   │   └── swissquote/
│   │       ├── SwissquoteApi.kt
│   │       └── SwissquoteModels.kt
│   │
│   ├── datastore/
│   │   └── UserPreferences.kt           # DataStore for settings
│   │
│   ├── util/
│   │   ├── CurrencyFormatter.kt          # Format Rp xxx.xxx
│   │   ├── DateUtils.kt
│   │   ├── GoldCalculator.kt             # Logam Mulia formula
│   │   └── ExcelExporter.kt             # Backup to .xlsx
│   │
│   └── ui/
│       ├── theme/
│       │   ├── Theme.kt                  # Material 3 theme
│       │   ├── Color.kt
│       │   └── Typography.kt
│       ├── navigation/
│       │   ├── AppNavHost.kt             # NavHost definition
│       │   ├── Screen.kt                 # Sealed class routes
│       │   └── BottomNavBar.kt
│       └── components/
│           ├── AccountPicker.kt          # Dropdown pilih account
│           ├── CategoryPicker.kt
│           ├── DatePickerField.kt
│           ├── AmountInput.kt
│           ├── IconPicker.kt
│           └── EmptyState.kt
│
├── domain/
│   ├── model/                            # Domain models (pure Kotlin)
│   │   ├── Account.kt
│   │   ├── AccountType.kt
│   │   ├── Category.kt
│   │   ├── Transaction.kt
│   │   ├── TransactionType.kt
│   │   ├── Budget.kt
│   │   ├── BudgetPeriod.kt
│   │   ├── RecurringTransaction.kt
│   │   ├── Frequency.kt
│   │   ├── Holding.kt
│   │   ├── AssetType.kt
│   │   ├── PortfolioTransaction.kt
│   │   ├── DividendRecord.kt
│   │   ├── DebtReceivable.kt
│   │   ├── DebtReceivableType.kt
│   │   ├── DebtReceivablePayment.kt
│   │   ├── PriceData.kt
│   │   └── TransactionSource.kt
│   │
│   └── repository/                       # Repository interfaces
│       ├── AccountRepository.kt
│       ├── CategoryRepository.kt
│       ├── TransactionRepository.kt
│       ├── BudgetRepository.kt
│       ├── RecurringRepository.kt
│       ├── HoldingRepository.kt
│       ├── PortfolioTransactionRepository.kt
│       ├── DividendRepository.kt
│       ├── PriceRepository.kt
│       ├── DebtReceivableRepository.kt
│       └── DashboardRepository.kt
│
├── feature/
│   ├── dashboard/
│   │   ├── DashboardScreen.kt
│   │   ├── DashboardViewModel.kt
│   │   └── components/
│   │       ├── NetWorthCard.kt
│   │       ├── MonthlySummaryCard.kt
│   │       ├── UpcomingDueCard.kt
│   │       └── RecentTransactionsList.kt
│   │
│   ├── expense/
│   │   ├── transaction/
│   │   │   ├── TransactionListScreen.kt
│   │   │   ├── TransactionListViewModel.kt
│   │   │   ├── AddEditTransactionScreen.kt
│   │   │   └── AddEditTransactionViewModel.kt
│   │   ├── account/
│   │   │   ├── AccountListScreen.kt
│   │   │   ├── AccountListViewModel.kt
│   │   │   ├── AddEditAccountScreen.kt
│   │   │   └── AddEditAccountViewModel.kt
│   │   ├── category/
│   │   │   ├── CategoryListScreen.kt
│   │   │   ├── CategoryListViewModel.kt
│   │   │   └── AddEditCategoryScreen.kt
│   │   ├── budget/
│   │   │   ├── BudgetScreen.kt
│   │   │   ├── BudgetViewModel.kt
│   │   │   └── AddEditBudgetScreen.kt
│   │   ├── statistics/
│   │   │   ├── StatisticsScreen.kt
│   │   │   └── StatisticsViewModel.kt
│   │   └── recurring/
│   │       ├── RecurringScreen.kt
│   │       ├── RecurringViewModel.kt
│   │       └── AddEditRecurringScreen.kt
│   │
│   ├── portfolio/
│   │   ├── overview/
│   │   │   ├── PortfolioOverviewScreen.kt
│   │   │   ├── PortfolioOverviewViewModel.kt
│   │   │   └── components/
│   │   │       ├── HoldingsList.kt
│   │   │       ├── PortfolioPieChart.kt
│   │   │       └── DailyPnlCard.kt
│   │   ├── holding/
│   │   │   ├── HoldingDetailScreen.kt
│   │   │   ├── HoldingDetailViewModel.kt
│   │   │   ├── AddEditHoldingScreen.kt
│   │   │   └── AddEditHoldingViewModel.kt
│   │   ├── dividend/
│   │   │   ├── DividendScreen.kt
│   │   │   └── DividendViewModel.kt
│   │   ├── analysis/
│   │   │   ├── AnalysisScreen.kt
│   │   │   └── AnalysisViewModel.kt
│   │   └── networth/
│   │       ├── NetWorthScreen.kt
│   │       └── NetWorthViewModel.kt
│   │
│   ├── debt/
│   │   ├── DebtListScreen.kt
│   │   ├── DebtListViewModel.kt
│   │   ├── DebtDetailScreen.kt
│   │   ├── DebtDetailViewModel.kt
│   │   ├── AddEditDebtScreen.kt
│   │   └── AddEditDebtViewModel.kt
│   │
│   └── settings/
│       ├── SettingsScreen.kt
│       ├── SettingsViewModel.kt
│       └── components/
│           ├── PasscodeSettings.kt
│           └── BackupRestore.kt
│
└── worker/
    ├── RecurringTransactionWorker.kt      # Process recurring transactions daily
    └── PriceRefreshWorker.kt              # Auto-refresh prices on schedule
```

## Dependency Flow

```
Screen (Compose)
    ↓ observes
ViewModel (Hilt injected)
    ↓ calls
Repository (interface in domain, impl in data)
    ↓ accesses
DAO (Room) / API (Retrofit) / DataStore
```

## Key Design Decisions

| Decision | Rationale |
|---|---|
| Single module | Simpler for solo dev, split later if needed |
| Room only (no cloud) | Offline-first, privacy, no backend cost |
| Compose Navigation | Native, type-safe routes with sealed class |
| Vico for charts | Compose-native, no AndroidView wrapping |
| Account as hub | All modules (expense, portfolio, debt) connect via Account balance |
| Unified Transaction | One table for all money movements, linked to holdings/debts via foreign keys |
