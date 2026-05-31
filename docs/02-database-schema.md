# CuangX Finance - Database Schema

## ER Diagram (Text)

```
┌──────────────┐     ┌──────────────────┐     ┌──────────────┐
│   Account    │     │   Transaction    │     │   Category   │
│──────────────│     │──────────────────│     │──────────────│
│ id (PK)      │◄────│ accountId (FK)   │     │ id (PK)      │
│ name         │     │ toAccountId (FK) │────►│ name         │
│ type         │     │ categoryId (FK)  │────►│ type         │
│ balance      │     │ type             │     │ icon         │
│ currency     │     │ amount           │     │ color        │
│ icon         │     │ date             │     │ parentId     │
│ color        │     │ note             │     │ sortOrder    │
│ creditLimit  │     │ photoUri         │     └──────────────┘
│ settlementDay│     │ isBookmarked     │
│ isArchived   │     │ linkedHoldingId  │──┐
│ sortOrder    │     │ linkedDividendId │──┤
│ createdAt    │     │ linkedDebtId     │──┤
└──────────────┘     │ linkedReceivable │──┤
       ▲             │ source           │  │
       │             │ createdAt        │  │
       │             └──────────────────┘  │
       │                                   │
       │   ┌──────────────────┐            │
       │   │     Holding      │◄───────────┘
       │   │──────────────────│
       │   │ id (PK)          │     ┌──────────────────────┐
       │   │ assetType        │     │ PortfolioTransaction │
       │   │ ticker           │◄────│──────────────────────│
       │   │ name             │     │ id (PK)              │
       │   │ quantity         │     │ holdingId (FK)       │
       │   │ avgBuyPrice      │     │ type                 │
       │   │ currency         │     │ quantity             │
       │   │ fundingAccountId │     │ price                │
       │   │ note             │     │ fee                  │
       │   │ createdAt        │     │ accountId            │
       │   └──────────────────┘     │ date                 │
       │            ▲               │ createdAt            │
       │            │               └──────────────────────┘
       │            │
       │   ┌──────────────────┐
       │   │ DividendRecord   │
       │   │──────────────────│
       │   │ id (PK)          │
       │   │ holdingId (FK)   │
       │   │ amount           │
       │   │ accountId        │
       │   │ date             │
       │   │ createdAt        │
       │   └──────────────────┘
       │
       │   ┌──────────────────┐     ┌───────────────────────────┐
       └───│ DebtReceivable   │     │ DebtReceivablePayment     │
           │──────────────────│     │───────────────────────────│
           │ id (PK)          │◄────│ id (PK)                   │
           │ type             │     │ debtId (FK)               │
           │ partyName        │     │ amount                    │
           │ originalAmount   │     │ accountId (FK)            │
           │ remainingAmount  │     │ transactionId (FK)        │
           │ currency         │     │ date                      │
           │ interestRate     │     │ createdAt                 │
           │ dateCreated      │     └───────────────────────────┘
           │ dueDate          │
           │ status           │
           │ note             │
           │ createdAt        │
           └──────────────────┘

┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│     Budget       │     │    Category      │     │    Account       │
│──────────────────│     │──────────────────│     │──────────────────│
│ id (PK)          │     │ id (PK)          │     │ id (PK)          │
│ categoryId (FK)  │────►│ name             │     │ name             │
│ amount           │     │ type             │     │ type             │
│ period           │     │ icon             │     │ balance          │
│ startDate        │     │ color            │     │ currency         │
│ isActive         │     │ parentId         │     └──────────────────┘
└──────────────────┘     │ sortOrder        │
                         └──────────────────┘

┌────────────────────────┐     ┌──────────────────┐
│ RecurringTransaction   │     │   PriceCache     │
│────────────────────────│     │──────────────────│
│ id (PK)                │     │ ticker (PK)      │
│ type                   │     │ price            │
│ amount                 │     │ currency         │
│ accountId (FK)         │     │ name             │
│ toAccountId (FK)       │     │ changePercent    │
│ categoryId (FK)        │     │ lastUpdated      │
│ frequency              │     └──────────────────┘
│ nextDate               │
│ isActive               │
│ note                   │
└────────────────────────┘
```

## Entity Details

### accounts

| Column | Type | Description |
|---|---|---|
| id | Long (PK, auto) | Primary key |
| name | String | Nama akun (BCA, GoPay, Cash) |
| type | String | CASH, BANK, CREDIT_CARD, E_WALLET, INVESTMENT |
| balance | Double | Saldo saat ini |
| currency | String | Mata uang (IDR) |
| icon | String | Nama icon |
| color | Long | Warna hex |
| creditLimit | Double? | Limit kartu kredit (nullable) |
| settlementDay | Int? | Tanggal settlement kartu kredit (nullable) |
| isArchived | Boolean | Sudah tidak aktif |
| sortOrder | Int | Urutan tampilan |
| createdAt | Long | Timestamp dibuat |

### categories

| Column | Type | Description |
|---|---|---|
| id | Long (PK, auto) | Primary key |
| name | String | Nama kategori |
| type | String | INCOME, EXPENSE |
| icon | String | Nama icon |
| color | Long | Warna hex |
| parentId | Long? | Parent category (null = top level) |
| sortOrder | Int | Urutan tampilan |

### transactions

| Column | Type | Description |
|---|---|---|
| id | Long (PK, auto) | Primary key |
| type | String | INCOME, EXPENSE, TRANSFER |
| amount | Double | Jumlah uang |
| accountId | Long (FK) | Akun sumber |
| toAccountId | Long? (FK) | Akun tujuan (untuk transfer) |
| categoryId | Long? (FK) | Kategori |
| date | Long | Tanggal transaksi |
| note | String | Catatan |
| photoUri | String? | Path foto struk |
| isBookmarked | Boolean | Bookmark untuk quick entry |
| linkedHoldingId | Long? | Link ke holding (portfolio) |
| linkedDividendId | Long? | Link ke dividend |
| linkedDebtId | Long? | Link ke utang |
| linkedReceivableId | Long? | Link ke piutang |
| source | String | EXPENSE, PORTFOLIO, DEBT |
| createdAt | Long | Timestamp dibuat |

### budgets

| Column | Type | Description |
|---|---|---|
| id | Long (PK, auto) | Primary key |
| categoryId | Long (FK) | Kategori yang di-budget |
| amount | Double | Jumlah budget |
| period | String | WEEKLY, MONTHLY, YEARLY |
| startDate | Long | Tanggal mulai budget |
| isActive | Boolean | Status aktif |

### recurring_transactions

| Column | Type | Description |
|---|---|---|
| id | Long (PK, auto) | Primary key |
| type | String | INCOME, EXPENSE, TRANSFER |
| amount | Double | Jumlah |
| accountId | Long (FK) | Akun sumber |
| toAccountId | Long? (FK) | Akun tujuan |
| categoryId | Long? (FK) | Kategori |
| frequency | String | DAILY, WEEKLY, MONTHLY, YEARLY |
| nextDate | Long | Tanggal eksekusi berikutnya |
| isActive | Boolean | Status aktif |
| note | String | Catatan |

### holdings

| Column | Type | Description |
|---|---|---|
| id | Long (PK, auto) | Primary key |
| assetType | String | STOCK, ETF, CRYPTO, GOLD, REAL_ESTATE, ART, COLLECTIBLE, OTHER |
| ticker | String? | Ticker symbol (BBCA.JK, BTC-USD, GC=F) |
| name | String | Nama aset |
| quantity | Double | Jumlah unit/lot/gram |
| avgBuyPrice | Double | Rata-rata harga beli |
| currency | String | Mata uang |
| fundingAccountId | Long? (FK) | Akun sumber dana |
| note | String | Catatan |
| createdAt | Long | Timestamp dibuat |

### portfolio_transactions

| Column | Type | Description |
|---|---|---|
| id | Long (PK, auto) | Primary key |
| holdingId | Long (FK) | Holding terkait |
| type | String | BUY, SELL |
| quantity | Double | Jumlah |
| price | Double | Harga per unit |
| fee | Double | Biaya transaksi |
| accountId | Long? (FK) | Akun sumber/tujuan dana |
| date | Long | Tanggal |
| createdAt | Long | Timestamp dibuat |

### dividend_records

| Column | Type | Description |
|---|---|---|
| id | Long (PK, auto) | Primary key |
| holdingId | Long (FK) | Holding terkait |
| amount | Double | Jumlah dividen |
| accountId | Long? (FK) | Akun tujuan penerimaan |
| date | Long | Tanggal |
| createdAt | Long | Timestamp dibuat |

### price_cache

| Column | Type | Description |
|---|---|---|
| ticker | String (PK) | Ticker symbol |
| price | Double | Harga terakhir |
| currency | String | Mata uang |
| name | String | Nama aset |
| changePercent | Double | Perubahan % hari ini |
| lastUpdated | Long | Terakhir diupdate |

### debts_receivables

| Column | Type | Description |
|---|---|---|
| id | Long (PK, auto) | Primary key |
| type | String | DEBT, RECEIVABLE |
| partyName | String | Nama pihak lawan |
| originalAmount | Double | Jumlah awal |
| remainingAmount | Double | Sisa yang belum dibayar/diterima |
| currency | String | Mata uang |
| interestRate | Double | Bunga (%) |
| dateCreated | Long | Tanggal dibuat |
| dueDate | Long? | Jatuh tempo |
| status | String | ACTIVE, PAID, OVERDUE |
| note | String | Catatan |
| createdAt | Long | Timestamp dibuat |

### debt_receivable_payments

| Column | Type | Description |
|---|---|---|
| id | Long (PK, auto) | Primary key |
| debtId | Long (FK) | Utang/Piutang terkait |
| amount | Double | Jumlah pembayaran |
| accountId | Long (FK) | Akun yang digunakan |
| transactionId | Long? (FK) | Transaksi yang auto-created |
| date | Long | Tanggal |
| createdAt | Long | Timestamp dibuat |

## Indexes

| Table | Index | Columns |
|---|---|---|
| transactions | idx_transactions_accountId | accountId |
| transactions | idx_transactions_toAccountId | toAccountId |
| transactions | idx_transactions_categoryId | categoryId |
| transactions | idx_transactions_date | date |
| portfolio_transactions | idx_portfolio_transactions_holdingId | holdingId |
| dividend_records | idx_dividend_records_holdingId | holdingId |
| debt_receivable_payments | idx_debt_payments_debtId | debtId |
| debt_receivable_payments | idx_debt_payments_accountId | accountId |
