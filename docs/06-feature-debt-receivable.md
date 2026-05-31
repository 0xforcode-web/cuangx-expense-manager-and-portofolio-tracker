# CuangX Finance - Feature: Utang & Piutang

## Overview

Modul Utang & Piutang (Debt & Receivables) memungkinkan user mencatat pinjaman uang yang diterima (utang) dan uang yang dipinjamkan ke orang lain (piutang). Terintegrasi penuh dengan Account — setiap transaksi utang/piutang otomatis mempengaruhi saldo akun.

## Prinsip Integrasi

```
UTANG (Debt/Liabilities):
  Terima uang (borrow)   → Account NAIK  → Utang NAIK
  Bayar utang (repay)    → Account TURUN → Utang TURUN

PIUTANG (Receivables):
  Kasih pinjam (lend)    → Account TURUN → Piutang NAIK
  Terima bayar (collect) → Account NAIK  → Piutang TURUN
```

## Data Model

### DebtReceivable

```kotlin
data class DebtReceivable(
    val id: Long = 0,
    val type: DebtReceivableType,     // DEBT or RECEIVABLE
    val partyName: String,            // "Ahmad", "Bank BRI"
    val originalAmount: Double,       // Jumlah awal
    val remainingAmount: Double,      // Sisa belum dibayar/diterima
    val currency: String = "IDR",
    val interestRate: Double = 0.0,   // Bunga % (opsional)
    val dateCreated: Long,
    val dueDate: Long? = null,        // Jatuh tempo
    val status: DebtStatus,           // ACTIVE, PAID, OVERDUE
    val note: String = ""
)

enum class DebtReceivableType { DEBT, RECEIVABLE }
enum class DebtStatus { ACTIVE, PAID, OVERDUE }
```

### DebtReceivablePayment

```kotlin
data class DebtReceivablePayment(
    val id: Long = 0,
    val debtId: Long,
    val amount: Double,
    val accountId: Long,              // Akun yang digunakan
    val transactionId: Long? = null,  // Auto-created transaction
    val date: Long
)
```

## Business Logic

### 1. Create Utang (Borrow Money)

```kotlin
suspend fun createDebt(debt: DebtReceivable, receiveAccountId: Long) {
    // 1. Insert debt record
    val debtId = debtReceivableDao.insert(debt.toEntity())
    
    // 2. Increase account balance
    accountDao.updateBalance(receiveAccountId, debt.originalAmount)
    
    // 3. Create linked transaction (INCOME)
    transactionRepository.createTransaction(
        Transaction(
            type = TransactionType.INCOME,
            amount = debt.originalAmount,
            accountId = receiveAccountId,
            categoryId = getCategoryId("Utang"),
            date = debt.dateCreated,
            note = "Utang dari ${debt.partyName}",
            linkedDebtId = debtId,
            source = TransactionSource.DEBT
        )
    )
}
```

### 2. Pay Utang (Repay Debt)

```kotlin
suspend fun payDebt(debtId: Long, amount: Double, payAccountId: Long, date: Long) {
    val debt = debtReceivableDao.getById(debtId)
    
    // Validate
    require(debt.remainingAmount >= amount) { "Pembayaran melebihi sisa utang" }
    require(accountDao.getBalance(payAccountId) >= amount) { "Saldo tidak cukup" }
    
    // 1. Insert payment record
    val payment = DebtReceivablePaymentEntity(
        debtId = debtId,
        amount = amount,
        accountId = payAccountId,
        date = date
    )
    val paymentId = debtReceivablePaymentDao.insert(payment)
    
    // 2. Decrease account balance
    accountDao.updateBalance(payAccountId, -amount)
    
    // 3. Update remaining amount
    val newRemaining = debt.remainingAmount - amount
    val newStatus = if (newRemaining <= 0) DebtStatus.PAID else DebtStatus.ACTIVE
    debtReceivableDao.updateRemaining(debtId, newRemaining, newStatus.name)
    
    // 4. Create linked transaction (EXPENSE)
    val transactionId = transactionRepository.createTransaction(
        Transaction(
            type = TransactionType.EXPENSE,
            amount = amount,
            accountId = payAccountId,
            categoryId = getCategoryId("Bayar Utang"),
            date = date,
            note = "Bayar utang ke ${debt.partyName}",
            linkedDebtId = debtId,
            source = TransactionSource.DEBT
        )
    )
    
    // 5. Link transaction to payment
    debtReceivablePaymentDao.updateTransactionId(paymentId, transactionId)
}
```

### 3. Create Piutang (Lend Money)

```kotlin
suspend fun createReceivable(receivable: DebtReceivable, fromAccountId: Long) {
    // Validate
    require(accountDao.getBalance(fromAccountId) >= receivable.originalAmount) { "Saldo tidak cukup" }
    
    // 1. Insert receivable record
    val receivableId = debtReceivableDao.insert(receivable.toEntity())
    
    // 2. Decrease account balance
    accountDao.updateBalance(fromAccountId, -receivable.originalAmount)
    
    // 3. Create linked transaction (EXPENSE - money goes out)
    transactionRepository.createTransaction(
        Transaction(
            type = TransactionType.EXPENSE,
            amount = receivable.originalAmount,
            accountId = fromAccountId,
            categoryId = getCategoryId("Piutang"),
            date = receivable.dateCreated,
            note = "Pinjamkan ke ${receivable.partyName}",
            linkedReceivableId = receivableId,
            source = TransactionSource.DEBT
        )
    )
}
```

### 4. Receive Piutang Payment

```kotlin
suspend fun receivePayment(receivableId: Long, amount: Double, receiveAccountId: Long, date: Long) {
    val receivable = debtReceivableDao.getById(receivableId)
    
    // Validate
    require(receivable.remainingAmount >= amount) { "Pembayaran melebihi sisa piutang" }
    
    // 1. Insert payment record
    val payment = DebtReceivablePaymentEntity(
        debtId = receivableId,
        amount = amount,
        accountId = receiveAccountId,
        date = date
    )
    val paymentId = debtReceivablePaymentDao.insert(payment)
    
    // 2. Increase account balance
    accountDao.updateBalance(receiveAccountId, amount)
    
    // 3. Update remaining amount
    val newRemaining = receivable.remainingAmount - amount
    val newStatus = if (newRemaining <= 0) DebtStatus.PAID else DebtStatus.ACTIVE
    debtReceivableDao.updateRemaining(receivableId, newRemaining, newStatus.name)
    
    // 4. Create linked transaction (INCOME - money comes in)
    val transactionId = transactionRepository.createTransaction(
        Transaction(
            type = TransactionType.INCOME,
            amount = amount,
            accountId = receiveAccountId,
            categoryId = getCategoryId("Terima Piutang"),
            date = date,
            note = "Terima piutang dari ${receivable.partyName}",
            linkedReceivableId = receivableId,
            source = TransactionSource.DEBT
        )
    )
    
    // 5. Link transaction to payment
    debtReceivablePaymentDao.updateTransactionId(paymentId, transactionId)
}
```

### 5. Overdue Detection

```kotlin
// Run daily (via WorkManager or on app open)
suspend fun checkOverdue() {
    val now = System.currentTimeMillis()
    debtReceivableDao.markOverdue(now)
}

// DAO query
@Query("""
    UPDATE debts_receivables 
    SET status = 'OVERDUE' 
    WHERE status = 'ACTIVE' AND dueDate IS NOT NULL AND dueDate < :now
""")
suspend fun markOverdue(now: Long)
```

## Screens

### 1. DebtListScreen

```
╔══════════════════════════════════════╗
║       UTANG & PIUTANG               ║
╠══════════════════════════════════════╣
║                                      ║
║  🔴 UTANG (harus bayar)             ║
║     Total: Rp 3,500,000             ║
║  ┌──────────────────────────────┐   ║
║  │ Ahmad        Rp 1,500,000    │   ║
║  │ Due: 5 Apr   ████████░░ 75%  │   ║
║  │                              │   ║
║  │ Bank BRI     Rp 2,000,000    │   ║
║  │ Due: 1 Dec   ██░░░░░░░░ 20%  │   ║
║  └──────────────────────────────┘   ║
║                                      ║
║  🟢 PIUTANG (akan diterima)         ║
║     Total: Rp 2,000,000             ║
║  ┌──────────────────────────────┐   ║
║  │ Budi         Rp 2,000,000    │   ║
║  │ Due: 10 Jul  ████████░░ 66%  │   ║
║  │                              │   ║
║  │ Rina         Rp    0         │   ║
║  │ Status: LUNAS ✓              │   ║
║  └──────────────────────────────┘   ║
║                                      ║
║  [+ Tambah Utang] [+ Tambah Piutang]║
╚══════════════════════════════════════╝
```

### 2. AddEditDebtScreen

```
╔══════════════════════════════════════╗
║       TAMBAH UTANG                  ║
╠══════════════════════════════════════╣
║                                      ║
║  Tipe:        [ Utang ▼ ]           ║
║  Nama Pihak:  [ Ahmad           ]    ║
║  Jumlah:      [ Rp 2,000,000   ]    ║
║  Masuk ke:    [ ▼ BCA           ]    ║  ← Account tujuan
║  Bunga:       [ 0%              ]    ║
║  Jatuh Tempo: [ 5 Apr 2026      ]    ║
║  Catatan:     [ Pinjam renovasi ]    ║
║                                      ║
║  [SIMPAN]                            ║
╚══════════════════════════════════════╝
```

### 3. DebtDetailScreen

```
╔══════════════════════════════════════╗
║  UTANG: Ahmad                       ║
╠══════════════════════════════════════╣
║                                      ║
║  Total:    Rp 2,000,000              ║
║  Dibayar:  Rp   500,000             ║
║  Sisa:     Rp 1,500,000             ║
║  Jatuh Tempo: 5 Apr 2026            ║
║                                      ║
║  ████████░░░░░░░░ 25%                ║
║                                      ║
║  Riwayat Pembayaran:                 ║
║  ┌──────────────────────────────┐   ║
║  │ 5 Feb  Rp 500,000  dari BCA  │   ║
║  └──────────────────────────────┘   ║
║                                      ║
║  [+ Bayar]   [✏ Edit]   [🗑 Hapus]  ║
╚══════════════════════════════════════╝
```

## Dashboard Integration

### Net Worth Impact

```kotlin
// Net Worth = Accounts + Portfolio + Piutang - Utang
fun calculateNetWorth(): Flow<Double> {
    return combine(
        accountRepository.getTotalBalance(),
        holdingRepository.getTotalPortfolioValue(),
        debtReceivableRepository.getTotalByType(DebtReceivableType.RECEIVABLE),
        debtReceivableRepository.getTotalByType(DebtReceivableType.DEBT)
    ) { accounts, portfolio, receivable, debt ->
        accounts + portfolio + receivable - debt
    }
}
```

### Upcoming Due Dates

```kotlin
// Show on dashboard: debts/receivables due within 7 days
fun getUpcomingDue(): Flow<List<DebtReceivable>> {
    val now = System.currentTimeMillis()
    val nextWeek = now + 7 * 24 * 60 * 60 * 1000
    return debtReceivableDao.getUpcomingDue(now, nextWeek)
}
```

### Account Ledger

Piutang/Utang transactions muncul di account ledger:

```
BCA Account Detail:
┌─────────────────────────────────────────────┐
│ Balance: Rp 7,745,000                       │
├─────────────────────────────────────────────┤
│ 1 Mar  Gaji              +Rp 10,000,000    │
│ 5 Mar  Pinjam (Utang)    +Rp  2,000,000    │  ←
│ 5 Mar  Makan             -Rp     50,000    │
│20 Mar  Bayar utang Ahmad -Rp    500,000    │  ←
│25 Mar  Dividen BBCA      +Rp  2,500,000    │
│ 1 Apr  Terima piutang    +Rp  2,000,000    │  ←
└─────────────────────────────────────────────┘
```

## File Summary

| File | Type | Description |
|---|---|---|
| DebtReceivableEntity.kt | Entity | Room entity |
| DebtReceivablePaymentEntity.kt | Entity | Room entity |
| DebtReceivableDao.kt | DAO | Database access |
| DebtReceivablePaymentDao.kt | DAO | Database access |
| DebtReceivableRepository.kt | Repository | Business logic |
| DebtListScreen.kt | Screen | List UI |
| DebtListViewModel.kt | ViewModel | List logic |
| DebtDetailScreen.kt | Screen | Detail UI |
| DebtDetailViewModel.kt | ViewModel | Detail logic |
| AddEditDebtScreen.kt | Screen | Form UI |
| AddEditDebtViewModel.kt | ViewModel | Form logic |
