# CuangX Finance - Implementation Roadmap

## Overview

Total estimated files: **~126 files**
Implementation phases: **9 phases**

## Phase 1: Project Skeleton & Database

**Goal:** Project setup, theme, navigation shell, Room database with all entities and DAOs.

**Files to create:**

| # | File | Type | Description |
|---|---|---|---|
| 1 | `settings.gradle.kts` | Config | Project settings |
| 2 | `build.gradle.kts` (project) | Config | Project-level build config |
| 3 | `app/build.gradle.kts` | Config | App-level build config with all dependencies |
| 4 | `CuangXApp.kt` | App | Application class with @HiltAndroidApp |
| 5 | `MainActivity.kt` | App | Single activity with Compose |
| 6 | `Color.kt` | UI | Color definitions (light/dark) |
| 7 | `Typography.kt` | UI | Typography definitions |
| 8 | `Theme.kt` | UI | Material 3 theme setup |
| 9 | `Screen.kt` | Navigation | Sealed class with all routes |
| 10 | `BottomNavBar.kt` | Navigation | Bottom navigation bar |
| 11 | `AppNavHost.kt` | Navigation | NavHost with all composable routes |
| 12 | `Converters.kt` | Database | Room type converters |
| 13 | `AccountEntity.kt` | Entity | Room entity |
| 14 | `CategoryEntity.kt` | Entity | Room entity |
| 15 | `TransactionEntity.kt` | Entity | Room entity |
| 16 | `BudgetEntity.kt` | Entity | Room entity |
| 17 | `RecurringTransactionEntity.kt` | Entity | Room entity |
| 18 | `HoldingEntity.kt` | Entity | Room entity |
| 19 | `PortfolioTransactionEntity.kt` | Entity | Room entity |
| 20 | `DividendRecordEntity.kt` | Entity | Room entity |
| 21 | `PriceCacheEntity.kt` | Entity | Room entity |
| 22 | `DebtReceivableEntity.kt` | Entity | Room entity |
| 23 | `DebtReceivablePaymentEntity.kt` | Entity | Room entity |
| 24 | `AccountDao.kt` | DAO | Database access |
| 25 | `CategoryDao.kt` | DAO | Database access |
| 26 | `TransactionDao.kt` | DAO | Database access |
| 27 | `BudgetDao.kt` | DAO | Database access |
| 28 | `RecurringTransactionDao.kt` | DAO | Database access |
| 29 | `HoldingDao.kt` | DAO | Database access |
| 30 | `PortfolioTransactionDao.kt` | DAO | Database access |
| 31 | `DividendRecordDao.kt` | DAO | Database access |
| 32 | `PriceCacheDao.kt` | DAO | Database access |
| 33 | `DebtReceivableDao.kt` | DAO | Database access |
| 34 | `DebtReceivablePaymentDao.kt` | DAO | Database access |
| 35 | `CuangXDatabase.kt` | Database | Room database definition |

**Deliverable:** App compiles, database creates all tables, navigation shell works with empty screens.

---

## Phase 2: Expense Core (Accounts, Categories, Transactions CRUD)

**Goal:** Full CRUD for accounts, categories, and transactions with account balance updates.

**Files to create:**

| # | File | Type | Description |
|---|---|---|---|
| 36 | `Account.kt` | Domain Model | Account data class |
| 37 | `AccountType.kt` | Domain Model | Enum |
| 38 | `Category.kt` | Domain Model | Category data class |
| 39 | `Transaction.kt` | Domain Model | Transaction data class |
| 40 | `TransactionType.kt` | Domain Model | Enum |
| 41 | `TransactionSource.kt` | Domain Model | Enum |
| 42 | `AccountRepository.kt` | Repository | Interface |
| 43 | `CategoryRepository.kt` | Repository | Interface |
| 44 | `TransactionRepository.kt` | Repository | Interface |
| 45 | `AccountRepositoryImpl.kt` | Repository | Implementation |
| 46 | `CategoryRepositoryImpl.kt` | Repository | Implementation |
| 47 | `TransactionRepositoryImpl.kt` | Repository | Implementation |
| 48 | `CurrencyFormatter.kt` | Utility | Format Rp xxx.xxx |
| 49 | `DateUtils.kt` | Utility | Date formatting |
| 50 | `AccountPicker.kt` | Component | Dropdown composable |
| 51 | `CategoryPicker.kt` | Component | Grid picker composable |
| 52 | `AmountInput.kt` | Component | Amount input composable |
| 53 | `DatePickerField.kt` | Component | Date picker composable |
| 54 | `EmptyState.kt` | Component | Empty state composable |
| 55 | `AccountListScreen.kt` | Screen | Account list UI |
| 56 | `AccountListViewModel.kt` | ViewModel | Account list logic |
| 57 | `AddEditAccountScreen.kt` | Screen | Account form UI |
| 58 | `AddEditAccountViewModel.kt` | ViewModel | Account form logic |
| 59 | `AccountDetailScreen.kt` | Screen | Account detail + ledger |
| 60 | `CategoryListScreen.kt` | Screen | Category list UI |
| 61 | `CategoryListViewModel.kt` | ViewModel | Category list logic |
| 62 | `AddEditCategoryScreen.kt` | Screen | Category form UI |
| 63 | `TransactionListScreen.kt` | Screen | Transaction list UI |
| 64 | `TransactionListViewModel.kt` | ViewModel | Transaction list logic |
| 65 | `AddEditTransactionScreen.kt` | Screen | Transaction form UI |
| 66 | `AddEditTransactionViewModel.kt` | ViewModel | Transaction form logic |

**Deliverable:** User can add accounts, categories, and transactions. Account balance updates automatically.

---

## Phase 3: Budget & Statistics

**Goal:** Budget management and statistical charts.

**Files to create:**

| # | File | Type | Description |
|---|---|---|---|
| 67 | `Budget.kt` | Domain Model | Budget data class |
| 68 | `BudgetPeriod.kt` | Domain Model | Enum |
| 69 | `BudgetRepository.kt` | Repository | Interface |
| 70 | `BudgetRepositoryImpl.kt` | Repository | Implementation |
| 71 | `BudgetScreen.kt` | Screen | Budget list with progress |
| 72 | `BudgetViewModel.kt` | ViewModel | Budget logic |
| 73 | `AddEditBudgetScreen.kt` | Screen | Budget form UI |
| 74 | `StatisticsScreen.kt` | Screen | Charts UI |
| 75 | `StatisticsViewModel.kt` | ViewModel | Statistics logic |

**Deliverable:** User can set budgets, see progress bars, view charts (income vs expense, pie chart by category).

---

## Phase 4: Recurring, Credit Card, Backup

**Goal:** Recurring transactions, credit card management, backup/restore.

**Files to create:**

| # | File | Type | Description |
|---|---|---|---|
| 76 | `RecurringTransaction.kt` | Domain Model | Data class |
| 77 | `Frequency.kt` | Domain Model | Enum |
| 78 | `RecurringRepository.kt` | Repository | Interface |
| 79 | `RecurringRepositoryImpl.kt` | Repository | Implementation |
| 80 | `RecurringScreen.kt` | Screen | Recurring list UI |
| 81 | `RecurringViewModel.kt` | ViewModel | Recurring logic |
| 82 | `AddEditRecurringScreen.kt` | Screen | Recurring form UI |
| 83 | `RecurringTransactionWorker.kt` | Worker | Background processing |
| 84 | `ExcelExporter.kt` | Utility | Backup/restore to Excel |
| 85 | `UserPreferences.kt` | DataStore | Settings storage |
| 86 | `SettingsScreen.kt` | Screen | Settings UI |
| 87 | `SettingsViewModel.kt` | ViewModel | Settings logic |
| 88 | `BackupRestore.kt` | Component | Backup/restore UI |

**Deliverable:** Recurring transactions auto-execute, credit card tracking works, backup/restore to Excel.

---

## Phase 5: Portfolio Core (Holdings, Price API)

**Goal:** Portfolio holdings with real-time prices from API.

**Files to create:**

| # | File | Type | Description |
|---|---|---|---|
| 89 | `Holding.kt` | Domain Model | Holding data class |
| 90 | `AssetType.kt` | Domain Model | Enum |
| 91 | `PortfolioTransaction.kt` | Domain Model | Data class |
| 92 | `PriceData.kt` | Domain Model | Price data |
| 93 | `HoldingRepository.kt` | Repository | Interface |
| 94 | `PortfolioTransactionRepository.kt` | Repository | Interface |
| 95 | `PriceRepository.kt` | Repository | Interface |
| 96 | `HoldingRepositoryImpl.kt` | Repository | Implementation |
| 97 | `PortfolioTransactionRepositoryImpl.kt` | Repository | Implementation |
| 98 | `PriceRepositoryImpl.kt` | Repository | Implementation |
| 99 | `YahooFinanceApi.kt` | API | Retrofit interface |
| 100 | `YahooFinanceModels.kt` | API | Response DTOs |
| 101 | `SwissquoteApi.kt` | API | Retrofit interface |
| 102 | `SwissquoteModels.kt` | API | Response DTOs |
| 103 | `GoldCalculator.kt` | Utility | Gold price formula |
| 104 | `RetrofitProvider.kt` | Network | DI module |
| 105 | `PortfolioOverviewScreen.kt` | Screen | Overview UI |
| 106 | `PortfolioOverviewViewModel.kt` | ViewModel | Overview logic |
| 107 | `HoldingsList.kt` | Component | Holdings list composable |
| 108 | `PortfolioPieChart.kt` | Component | Pie chart composable |
| 109 | `DailyPnlCard.kt` | Component | P&L card composable |
| 110 | `AddEditHoldingScreen.kt` | Screen | Holding form UI |
| 111 | `AddEditHoldingViewModel.kt` | ViewModel | Holding form logic |
| 112 | `HoldingDetailScreen.kt` | Screen | Holding detail UI |
| 113 | `HoldingDetailViewModel.kt` | ViewModel | Holding detail logic |

**Deliverable:** User can add holdings (stocks, gold, crypto, etc.), see real-time prices, P&L, buy/sell transactions.

---

## Phase 6: Portfolio Full (Dividends, Analysis, Net Worth)

**Goal:** Complete portfolio features.

**Files to create:**

| # | File | Type | Description |
|---|---|---|---|
| 114 | `DividendRecord.kt` | Domain Model | Data class |
| 115 | `DividendRepository.kt` | Repository | Interface |
| 116 | `DividendRepositoryImpl.kt` | Repository | Implementation |
| 117 | `DividendScreen.kt` | Screen | Dividend UI |
| 118 | `DividendViewModel.kt` | ViewModel | Dividend logic |
| 119 | `AnalysisScreen.kt` | Screen | Analysis UI |
| 120 | `AnalysisViewModel.kt` | ViewModel | Analysis logic |
| 121 | `NetWorthScreen.kt` | Screen | Net worth UI |
| 122 | `NetWorthViewModel.kt` | ViewModel | Net worth logic |

**Deliverable:** Dividend tracking, portfolio analysis by region/industry/asset type, net worth tracking.

---

## Phase 7: Utang & Piutang

**Goal:** Debt and receivables management with account integration.

**Files to create:**

| # | File | Type | Description |
|---|---|---|---|
| 123 | `DebtReceivable.kt` | Domain Model | Data class |
| 124 | `DebtReceivableType.kt` | Domain Model | Enum |
| 125 | `DebtReceivablePayment.kt` | Domain Model | Data class |
| 126 | `DebtStatus.kt` | Domain Model | Enum |
| 127 | `DebtReceivableRepository.kt` | Repository | Interface |
| 128 | `DebtReceivableRepositoryImpl.kt` | Repository | Implementation |
| 129 | `DebtListScreen.kt` | Screen | Debt list UI |
| 130 | `DebtListViewModel.kt` | ViewModel | Debt list logic |
| 131 | `AddEditDebtScreen.kt` | Screen | Debt form UI |
| 132 | `AddEditDebtViewModel.kt` | ViewModel | Debt form logic |
| 133 | `DebtDetailScreen.kt` | Screen | Debt detail UI |
| 134 | `DebtDetailViewModel.kt` | ViewModel | Debt detail logic |

**Deliverable:** User can add debts/receivables, make payments, see remaining amounts, overdue alerts.

---

## Phase 8: Dashboard & Polish

**Goal:** Unified dashboard, passcode/biometric, price refresh worker.

**Files to create:**

| # | File | Type | Description |
|---|---|---|---|
| 135 | `DashboardRepository.kt` | Repository | Interface |
| 136 | `DashboardRepositoryImpl.kt` | Repository | Implementation |
| 137 | `DashboardScreen.kt` | Screen | Dashboard UI |
| 138 | `DashboardViewModel.kt` | ViewModel | Dashboard logic |
| 139 | `NetWorthCard.kt` | Component | Net worth card composable |
| 140 | `MonthlySummaryCard.kt` | Component | Monthly summary composable |
| 141 | `UpcomingDueCard.kt` | Component | Upcoming due composable |
| 142 | `RecentTransactionsList.kt` | Component | Recent transactions composable |
| 143 | `PriceRefreshWorker.kt` | Worker | Background price refresh |
| 144 | `PasscodeSettings.kt` | Component | Passcode/biometric UI |
| 145 | `IconPicker.kt` | Component | Icon picker composable |

**Deliverable:** Dashboard shows unified net worth, upcoming dues, recent transactions. Passcode lock works.

---

## Phase 9: Testing & Release

**Goal:** Testing, bug fixes, UI polish.

**Activities:**
- Unit tests for repositories
- UI tests for critical flows
- Edge case handling (negative balance, duplicate transactions)
- Performance optimization (large dataset queries)
- UI polish (animations, transitions)
- ProGuard/R8 configuration
- App signing and release build

---

## File Count Summary

| Phase | Files | Cumulative |
|---|---|---|
| Phase 1: Skeleton & DB | 35 | 35 |
| Phase 2: Expense Core | 31 | 66 |
| Phase 3: Budget & Stats | 9 | 75 |
| Phase 4: Recurring & Backup | 13 | 88 |
| Phase 5: Portfolio Core | 25 | 113 |
| Phase 6: Portfolio Full | 9 | 122 |
| Phase 7: Utang & Piutang | 12 | 134 |
| Phase 8: Dashboard & Polish | 11 | 145 |
| Phase 9: Testing | (test files) | ~155 |

## Dependencies Between Phases

```
Phase 1 (Skeleton)
    │
    ├──→ Phase 2 (Expense Core)
    │       │
    │       ├──→ Phase 3 (Budget & Stats)
    │       │
    │       └──→ Phase 4 (Recurring & Backup)
    │
    ├──→ Phase 5 (Portfolio Core)
    │       │
    │       └──→ Phase 6 (Portfolio Full)
    │
    └──→ Phase 7 (Utang & Piutang)
            │
            └──→ Phase 8 (Dashboard - depends on all above)
                    │
                    └──→ Phase 9 (Testing)
```

**Parallel work possible:**
- Phase 3 & Phase 4 can be done in parallel
- Phase 5, 6, 7 can be started after Phase 2 (independent of Phase 3/4)
- Phase 8 requires all previous phases to be complete
