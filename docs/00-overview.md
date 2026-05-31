# CuangX Finance - Overview

## Deskripsi

CuangX Finance adalah aplikasi Android yang menggabungkan **Expense Manager** dan **Portfolio Tracker** dalam satu aplikasi terintegrasi. Semua fitur terhubung melalui **Account** sebagai hub pusat, sehingga pengguna memiliki gambaran lengkap tentang keuangannya.

## Referensi Aplikasi

| Aplikasi | Fitur Utama |
|---|---|
| [Money Manager](https://play.google.com/store/apps/details?id=com.realbyteapps.moneymanagerfree) | Double-entry bookkeeping, budget, kartu kredit, transfer, recurring, statistik, backup |
| [getquin](https://play.google.com/store/apps/details?id=com.getquin.app) | Portfolio tracker, dividend tracker, portfolio analysis, net worth tracking |

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture (3 layers) |
| DI | Hilt |
| Database | Room (local only) |
| Preferences | DataStore |
| Networking | Retrofit + OkHttp + Moshi |
| Async | Kotlin Coroutines + Flow |
| Charts | Vico (Compose-native charting library) |
| Background Work | WorkManager (recurring transactions, price refresh) |
| Image Loading | Coil |
| Excel Export | Apache POI |
| Biometric | AndroidX Biometric |

## Fitur Utama

### 1. Expense Manager (Full)
- Double-entry bookkeeping
- Kategori & sub-kategori dengan icon dan warna
- Budget per kategori (mingguan/bulanan/tahunan)
- Kartu kredit (limit, outstanding, settlement date)
- Transfer antar akun
- Transaksi berulang otomatis
- Statistik & grafik (pie chart, bar chart, line chart)
- Bookmark transaksi favorit
- Backup/Restore ke Excel (.xlsx)
- Passcode / biometric lock

### 2. Portfolio Tracker (Full)
- Aset: Stocks, ETFs, Crypto, Logam Mulia, Real Estate, Art, Collectibles
- Harga real-time dari API (Yahoo Finance, Swissquote)
- Buy/Sell transaction tracking
- Dividend tracker dengan calendar & forecast
- Portfolio analysis (by region, industry, asset class)
- Net worth tracking
- P&L (unrealized & realized)
- Logam Mulia: harga per gram dalam Rupiah

### 3. Utang & Piutang (Debt & Receivables)
- Catat utang (pinjam uang) dan piutang (kasih pinjam)
- Cicilan parsial
- Jatuh tempo & reminder
- Status: ACTIVE, PAID, OVERDUE
- Terintegrasi penuh dengan Account

### 4. Dashboard Terintegrasi
- Total net worth (accounts + portfolio + piutang - utang)
- Ringkasan cashflow bulan ini
- Upcoming due dates
- Recent transactions dari semua modul

## Data Sources (APIs)

| Data | API | Endpoint |
|---|---|---|
| Stock/ETF/Crypto/Gold Futures | Yahoo Finance | `https://query1.finance.yahoo.com/v8/finance/chart/{TICKER}` |
| XAU/USD (Gold spot) | Swissquote | `https://forex-data-feed.swissquote.com/public-quotes/bboquotes/instrument/XAU/USD` |
| USD/IDR | Yahoo Finance | `https://query1.finance.yahoo.com/v8/finance/chart/USDIDR=X` |

## Logam Mulia Formula

```
Harga per gram (IDR) = (Gold_XAU_USD / 31.1035) * USD_IDR
```

- `Gold_XAU_USD`: Harga gold per troy ounce dari Yahoo Finance (ticker: `GC=F`)
- `USD_IDR`: Kurs Rupiah dari Yahoo Finance (ticker: `USDIDR=X`)
- `31.1035`: Konversi troy ounce ke gram

## Target

- Platform: Android (minSdk 26)
- Bahasa: Indonesia (default), English (opsional)
- Mata uang default: IDR
- Offline-first: semua data lokal, API hanya untuk harga real-time
