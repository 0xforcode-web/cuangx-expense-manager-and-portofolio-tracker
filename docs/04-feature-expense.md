# CuangX Finance - Feature: Expense Manager

## Overview

Modul Expense Manager menangani semua aspek pencatatan keuangan harian: pemasukan, pengeluaran, transfer antar akun, budgeting, dan pelaporan.

## Double-Entry Bookkeeping

Setiap transaksi langsung mempengaruhi saldo akun:

| Transaksi | Account Effect |
|---|---|
| INCOME | `account.balance += amount` |
| EXPENSE | `account.balance -= amount` |
| TRANSFER | `fromAccount.balance -= amount`, `toAccount.balance += amount` |

## Features

### 1. Accounts

**Tipe Akun:**
| Type | Description | Special Fields |
|---|---|---|
| CASH | Uang tunai | - |
| BANK | Rekening bank | - |
| CREDIT_CARD | Kartu kredit | creditLimit, settlementDay |
| E_WALLET | E-wallet (GoPay, OVO) | - |
| INVESTMENT | Akun investasi/broker | - |

**Screens:**
- **AccountListScreen** — List semua akun dengan saldo, total di atas
- **AddEditAccountScreen** — Form tambah/edit akun
- **AccountDetailScreen** — Detail akun + ledger transaksi

**ViewModel:**
```kotlin
@HiltViewModel
class AccountListViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    val accounts = accountRepository.getAllAccounts()
    val totalBalance = accountRepository.getTotalBalance()
    
    fun addAccount(account: Account) { ... }
    fun updateAccount(account: Account) { ... }
    fun archiveAccount(id: Long) { ... }
    fun deleteAccount(id: Long) { ... }
}
```

### 2. Categories

**Struktur:**
- Parent category (Makan, Transport, Belanja)
- Sub-category (Makan → Restoran, Warung, Delivery)

**Default Categories (pre-populated):**

| Type | Category | Sub-categories |
|---|---|---|
| EXPENSE | Makan & Minum | Restoran, Warung, Delivery, Kopi |
| EXPENSE | Transport | Bensin, Parkir, Ojol, Tol |
| EXPENSE | Belanja | Supermarket, Pakaian, Elektronik |
| EXPENSE | Rumah | Cicilan, Listrik, Air, Internet |
| EXPENSE | Kesehatan | Obat, Dokter, Gym |
| EXPENSE | Hiburan | Film, Game, Liburan |
| EXPENSE | Pendidikan | SPP, Buku, Kursus |
| EXPENSE | Lainnya | - |
| INCOME | Gaji | - |
| INCOME | Bonus | - |
| INCOME | Investasi | Dividen, Capital Gain |
| INCOME | Hadiah | - |
| INIMATE | Lainnya | - |

**Screens:**
- **CategoryListScreen** — List kategori per tipe, expand sub-category
- **AddEditCategoryScreen** — Form + icon picker + color picker

### 3. Transactions

**Tipe Transaksi:**
| Type | Description | Required Fields |
|---|---|---|
| INCOME | Pemasukan | amount, accountId, categoryId, date |
| EXPENSE | Pengeluaran | amount, accountId, categoryId, date |
| TRANSFER | Transfer antar akun | amount, fromAccountId, toAccountId, date |

**Screens:**
- **TransactionListScreen**
  - List transaksi grouped by tanggal
  - Filter: date range, category, account, type
  - Search by note
  - Swipe to delete
  - Pull to refresh

- **AddEditTransactionScreen**
  - Tab selector: INCOME / EXPENSE / TRANSFER
  - Amount input (calculator-style keyboard)
  - Account picker (dropdown)
  - Category picker (grid dengan icon)
  - Date picker
  - Note input
  - Photo attachment (optional)
  - Bookmark toggle

**Business Logic:**
```kotlin
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao
) : TransactionRepository {

    override suspend fun createTransaction(transaction: Transaction) {
        // 1. Insert transaction
        transactionDao.insert(transaction.toEntity())
        
        // 2. Update account balance
        when (transaction.type) {
            TransactionType.INCOME -> {
                accountDao.updateBalance(transaction.accountId, transaction.amount)
            }
            TransactionType.EXPENSE -> {
                accountDao.updateBalance(transaction.accountId, -transaction.amount)
            }
            TransactionType.TRANSFER -> {
                accountDao.updateBalance(transaction.accountId, -transaction.amount)
                accountDao.updateBalance(transaction.toAccountId!!, transaction.amount)
            }
        }
    }
}
```

### 4. Budget

**Budget Period:**
| Period | Description |
|---|---|
| WEEKLY | Per minggu (Senin-Minggu) |
| MONTHLY | Per bulan (tanggal 1-akhir) |
| YEARLY | Per tahun |

**Screens:**
- **BudgetScreen**
  - List budget dengan progress bar (terpakai vs budget)
  - Category icon + name
  - Amount: Rp xxx,xxx / Rp x,xxx,xxx
  - Color: hijau (< 80%), kuning (80-100%), merah (> 100%)
  - Filter by period

- **AddEditBudgetScreen**
  - Category picker
  - Amount input
  - Period selector
  - Start date

**Business Logic:**
```kotlin
// Calculate spent amount for budget
fun getBudgetProgress(budgetId: Long, startDate: Long, endDate: Long): Flow<BudgetProgress> {
    return combine(
        budgetDao.getBudget(budgetId),
        transactionDao.getTotalByCategoryAndDateRange(categoryId, startDate, endDate)
    ) { budget, spent ->
        BudgetProgress(
            budget = budget,
            spent = spent,
            remaining = budget.amount - spent,
            percentage = (spent / budget.amount * 100).toInt()
        )
    }
}
```

### 5. Recurring Transactions

**Frequency:**
| Frequency | Description |
|---|---|
| DAILY | Setiap hari |
| WEEKLY | Setiap minggu (hari yang sama) |
| MONTHLY | Setiap bulan (tanggal yang sama) |
| YEARLY | Setiap tahun (tanggal yang sama) |

**Use Cases:**
- Gaji bulanan (INCOME, MONTHLY)
- Cicilan rumah (EXPENSE, MONTHLY)
- Langganan streaming (EXPENSE, MONTHLY)
- Tabungan rutin (TRANSFER, MONTHLY)

**Screens:**
- **RecurringScreen** — List recurring transactions, status aktif/nonaktif
- **AddEditRecurringScreen** — Form seperti transaksi biasa + frequency picker

**Worker Implementation:**
```kotlin
class RecurringTransactionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val recurringRepository: RecurringRepository,
    private val transactionRepository: TransactionRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dueRecurring = recurringRepository.getDueRecurring()
        
        for (recurring in dueRecurring) {
            // Create actual transaction
            transactionRepository.createTransaction(
                recurring.toTransaction()
            )
            
            // Update nextDate
            recurringRepository.updateNextDate(
                recurring.id,
                calculateNextDate(recurring.frequency, recurring.nextDate)
            )
        }
        
        return Result.success()
    }
}
```

### 6. Statistics

**Charts:**
| Chart | Type | Data |
|---|---|---|
| Income vs Expense | Bar chart | Monthly comparison |
| Expense by Category | Pie chart | Current month breakdown |
| Balance Trend | Line chart | Account balance over time |
| Top Expenses | List | Top 5 categories by amount |

**Screens:**
- **StatisticsScreen**
  - Period selector (minggu/bulan/tahun)
  - Bar chart: income vs expense
  - Pie chart: expense per kategori
  - Line chart: trend saldo
  - List: top kategori pengeluaran

### 7. Backup & Restore

**Backup Format:** Excel (.xlsx)

**Sheets:**
1. **Accounts** — semua akun dengan saldo
2. **Categories** — semua kategori
3. **Transactions** — semua transaksi dengan detail
4. **Budgets** — semua budget
5. **Holdings** — portfolio holdings
6. **Debts** — utang & piutang

**Implementation:**
```kotlin
class ExcelExporter @Inject constructor(
    private val context: Context
) {
    suspend fun exportBackup(data: BackupData): Uri {
        val workbook = XSSFWorkbook()
        
        // Sheet 1: Accounts
        val accountSheet = workbook.createSheet("Accounts")
        // ... write headers and data
        
        // Sheet 2: Transactions
        val transactionSheet = workbook.createSheet("Transactions")
        // ... write headers and data
        
        // Save to file
        val file = File(context.cacheDir, "cuangx_backup_${System.currentTimeMillis()}.xlsx")
        FileOutputStream(file).use { workbook.write(it) }
        workbook.close()
        
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    suspend fun importBackup(uri: Uri): Result<Unit> {
        // Parse Excel file
        // Validate data
        // Insert into database
        // Return success/error
    }
}
```

### 8. Settings

| Setting | Type | Description |
|---|---|---|
| Passcode | Boolean + String | Enable/disable passcode lock |
| Biometric | Boolean | Enable fingerprint/face unlock |
| Start Day | Int (1-28) | Hari mulai bulan keuangan |
| Default Currency | String | Mata uang default (IDR) |
| Theme | Enum | System, Light, Dark |
| Backup Reminder | Boolean | Reminder untuk backup mingguan |

## File Summary

| File | Type | Description |
|---|---|---|
| AccountEntity.kt | Entity | Room entity |
| AccountDao.kt | DAO | Database access |
| AccountRepository.kt | Repository | Business logic |
| AccountListScreen.kt | Screen | Compose UI |
| AccountListViewModel.kt | ViewModel | State management |
| AddEditAccountScreen.kt | Screen | Form UI |
| AddEditAccountViewModel.kt | ViewModel | Form logic |
| CategoryEntity.kt | Entity | Room entity |
| CategoryDao.kt | DAO | Database access |
| CategoryRepository.kt | Repository | Business logic |
| CategoryListScreen.kt | Screen | Compose UI |
| AddEditCategoryScreen.kt | Screen | Form UI |
| TransactionEntity.kt | Entity | Room entity |
| TransactionDao.kt | DAO | Database access |
| TransactionRepository.kt | Repository | Business logic |
| TransactionListScreen.kt | Screen | List UI |
| TransactionListViewModel.kt | ViewModel | List logic |
| AddEditTransactionScreen.kt | Screen | Form UI |
| AddEditTransactionViewModel.kt | ViewModel | Form logic |
| BudgetEntity.kt | Entity | Room entity |
| BudgetDao.kt | DAO | Database access |
| BudgetRepository.kt | Repository | Business logic |
| BudgetScreen.kt | Screen | Budget UI |
| BudgetViewModel.kt | ViewModel | Budget logic |
| AddEditBudgetScreen.kt | Screen | Form UI |
| StatisticsScreen.kt | Screen | Charts UI |
| StatisticsViewModel.kt | ViewModel | Stats logic |
| RecurringTransactionEntity.kt | Entity | Room entity |
| RecurringTransactionDao.kt | DAO | Database access |
| RecurringRepository.kt | Repository | Business logic |
| RecurringScreen.kt | Screen | List UI |
| RecurringViewModel.kt | ViewModel | List logic |
| AddEditRecurringScreen.kt | Screen | Form UI |
| RecurringTransactionWorker.kt | Worker | Background processing |
| ExcelExporter.kt | Utility | Backup/restore |
| UserPreferences.kt | DataStore | Settings storage |
| SettingsScreen.kt | Screen | Settings UI |
| SettingsViewModel.kt | ViewModel | Settings logic |
