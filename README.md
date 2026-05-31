# CuangX Finance

<div align="center">

**Expense Manager + Portfolio Tracker**

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-purple.svg)](https://developer.android.com/jetpack/compose)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

*A comprehensive personal finance app that combines expense management with investment portfolio tracking.*

</div>

---

## 📱 Features

### 💰 Expense Manager
- **Double-entry bookkeeping** — Catatan keuangan lengkap
- **Kategori & Sub-kategori** — Kelompokkan transaksi
- **Budget per kategori** — Kontrol pengeluaran
- **Recurring transactions** — Transaksi otomatis
- **Statistik & Grafik** — Analisis keuangan visual

### 📈 Portfolio Tracker
- **Multi-asset support** — Stocks, ETF, Crypto, Gold (Logam Mulia), Real Estate
- **Trading Journal** — Catatan keputusan investasi (source of truth)
- **Real-time prices** — Harga dari Yahoo Finance API
- **P&L tracking** — Unrealized & realized profit
- **Dividend tracker** — Pantau dividen masuk

### 💳 Utang & Piutang
- **Debt tracking** — Catat utang yang harus dibayar
- **Receivable tracking** — Catat piutang yang akan diterima
- **Payment history** — Riwayat pembayaran
- **Auto account sync** — Terintegrasi dengan Account

### 📊 Dashboard Terintegrasi
- **Net Worth** — Total kekayaan bersih
- **Cashflow bulanan** — Income vs Expense
- **Portfolio value** — Nilai investasi saat ini
- **Upcoming dues** — Jatuh tempo dekat

### ⚙️ Pengaturan
- **Multiple account types** — Cash, Bank, E-Wallet, Broker, Credit Card
- **Passcode/Biometric lock** — Keamanan aplikasi
- **Backup/Restore** — Export ke Excel (.xlsx)
- **Dark/Light mode** — Tema sesuai preferensi

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Database | Room (local only) |
| Networking | Retrofit + Moshi |
| Charts | Vico |
| Background | WorkManager |

---

## 📦 Data Sources

| Data | API | Endpoint |
|------|-----|----------|
| Stocks/ETF/Crypto | Yahoo Finance | `query1.finance.yahoo.com/v8/finance/chart/{TICKER}` |
| Gold Price | Yahoo Finance | `GC=F` (Gold Futures) |
| USD/IDR Rate | Yahoo Finance | `USDIDR=X` |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest stable)
- Android SDK 26+
- Java 17+

### Installation

1. **Clone repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/cuangx-finance.git
   cd cuangx-finance
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Build & Run**
   - Sync Gradle
   - Run on device/emulator

---

## 📂 Project Structure

```
app/src/main/java/com/cuangx/finance/
├── core/
│   ├── database/        # Room entities, DAOs, migrations
│   ├── network/         # Retrofit APIs, models
│   ├── ui/              # Theme, navigation, components
│   ├── datastore/       # Preferences
│   └── util/            # Utilities
├── domain/
│   ├── model/           # Domain models
│   └── repository/      # Repository interfaces
├── data/
│   └── repository/      # Repository implementations
├── feature/
│   ├── dashboard/       # Dashboard screen
│   ├── expense/         # Expense management
│   ├── portfolio/       # Portfolio & Journal
│   ├── debt/            # Utang & Piutang
│   └── settings/        # Settings
└── worker/              # Background workers
```

---

## 🔄 App Flow

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   JOURNAL   │────►│ TRANSACTION │────►│   ACCOUNT   │
│  (Input)    │     │   (Auto)    │     │  (Balance)  │
└─────────────┘     └─────────────┘     └─────────────┘
       │                                       │
       ▼                                       ▼
┌─────────────┐                         ┌─────────────┐
│  PORTFOLIO  │                         │  DASHBOARD  │
│ (Positions) │                         │ (Net Worth) │
└─────────────┘                         └─────────────┘
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🤝 Contributing

Contributions are welcome! Please read the [Contributing Guidelines](CONTRIBUTING.md) first.

---

## 📞 Contact

**CuangX-by-fachriceg**
- GitHub: [@YOUR_USERNAME](https://github.com/YOUR_USERNAME)

---

## 🙏 Acknowledgments

- [Yahoo Finance API](https://finance.yahoo.com/) for market data
- [Material Design 3](https://m3.material.io/) for UI components
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern UI toolkit
