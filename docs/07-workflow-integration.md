# CuangX Finance - Workflow Integration

## Core Principle: Account sebagai Hub

Semua modul (Expense, Portfolio, Utang/Piutang) terhubung melalui **Account**. Account adalah pusat dari semua aliran uang. Setiap pergerakan uang — baik itu belanja harian, beli saham, terima dividen, pinjam uang, atau bayar utang — semuanya tercatat dan mempengaruhi saldo akun.

```
                         ┌──────────┐
                         │ ACCOUNT  │
                         │  (Hub)   │
                         └────┬─────┘
                              │
          ┌───────────┬───────┼───────┬───────────┐
          │           │       │       │           │
     ┌────▼───┐ ┌────▼───┐ ┌─▼──┐ ┌──▼──┐ ┌────▼───┐
     │EXPENSE │ │PORTFOLIO│ │UTANG│ │PIU- │ │TRANSFER│
     │        │ │        │ │    │ │TANG │ │        │
     │ Gaji   │ │ Buy    │ │Pin- │ │Kasih│ │ BCA↔   │
     │ Makan  │ │ Sell   │ │jam  │ │pin- │ │ GoPay  │
     │ Cicilan│ │ Dividen│ │Bayar│ │jam  │ │        │
     └────────┘ └────────┘ └─────┘ └─────┘ └────────┘
          │           │       │       │           │
          └───────────┴───────┴───────┴───────────┘
                              │
                    Semua tercatat di
                    unified Transaction
```

## Unified Transaction Table

Satu tabel `transactions` untuk SEMUA pergerakan uang, dengan field `source` dan `linkedXxxId` untuk menghubungkan ke modul asal.

```
Transaction:
┌──────────────────────────────────────────────────┐
│ id, type, amount, accountId, toAccountId,        │
│ categoryId, date, note,                          │
│ linkedHoldingId,       ← dari Portfolio          │
│ linkedDividendId,      ← dari Portfolio          │
│ linkedDebtId,          ← dari Utang              │
│ linkedReceivableId,    ← dari Piutang            │
│ source                 ← EXPENSE/PORTFOLIO/DEBT  │
└──────────────────────────────────────────────────┘
```

## Contoh Workflow Lengkap

### Scenario: Sebulan dalam Hidup User

#### 1. Gaji Masuk (Expense Module)
```
Date: 1 Jan 2026
Type: INCOME
Amount: Rp 10,000,000
To Account: BCA
Category: Gaji

→ BCA balance: 0 → Rp 10,000,000
→ Transaction: INCOME to BCA, source=EXPENSE
```

#### 2. Bayar Cicilan (Expense Module)
```
Date: 2 Jan 2026
Type: EXPENSE
Amount: Rp 2,500,000
From Account: BCA
Category: Cicilan Rumah

→ BCA balance: Rp 10,000,000 → Rp 7,500,000
→ Transaction: EXPENSE from BCA, source=EXPENSE
```

#### 3. Belanja Harian (Expense Module)
```
Date: 3-31 Jan 2026 (multiple transactions)
Type: EXPENSE
Amount: total Rp 3,000,000
From Account: BCA / GoPay
Category: Makan, Transport, Belanja, dll

→ BCA balance: Rp 7,500,000 → Rp 4,500,000 (sisa)
→ Multiple transactions: EXPENSE, source=EXPENSE
```

#### 4. Pinjam Uang (Utang)
```
Date: 5 Jan 2026
Action: BORROW
From: Teman (Ahmad)
Amount: Rp 2,000,000
To Account: BCA

→ BCA balance: Rp 4,500,000 → Rp 6,500,000
→ Debt record: Rp 2,000,000 (remaining: Rp 2,000,000)
→ Transaction: INCOME to BCA, category="Utang", source=DEBT
```

#### 5. Beli Saham (Portfolio Module)
```
Date: 15 Jan 2026
Action: BUY
Asset: BBCA (Bank BCA)
Qty: 100 lot @ Rp 9,000
Total: Rp 900,000 + fee Rp 5,000

→ BCA balance: Rp 6,500,000 → Rp 5,595,000
→ Holding: BBCA, qty=100, avgBuyPrice=9,000
→ Transaction: TRANSFER from BCA, source=PORTFOLIO
```

#### 6. Kasih Pinjam (Piutang)
```
Date: 10 Jan 2026
Action: LEND
To: Adik (Budi)
Amount: Rp 3,000,000
From Account: BCA

→ BCA balance: Rp 5,595,000 → Rp 2,595,000
→ Receivable record: Rp 3,000,000 (remaining: Rp 3,000,000)
→ Transaction: EXPENSE from BCA, category="Piutang", source=DEBT
```

#### 7. Beli Logam Mulia (Portfolio Module)
```
Date: 20 Jan 2026
Action: BUY
Asset: Logam Mulia
Qty: 2 gram
Price: auto-fetched → Rp 1,571,830/gram
Total: Rp 3,143,660

Check: BCA balance Rp 2,595,000 < Rp 3,143,660
→ Warning: "Saldo tidak cukup. Kurangi quantity atau pilih account lain."

User kurangi ke 1 gram = Rp 1,571,830
→ BCA balance: Rp 2,595,000 → Rp 1,023,170
→ Holding: GOLD, qty=1, avgBuyPrice=1,571,830
→ Transaction: TRANSFER from BCA, source=PORTFOLIO
```

#### 8. Dividen Saham Masuk (Portfolio → Account)
```
Date: 25 Feb 2026
Event: BBCA dividend
Amount: Rp 250/share × 10,000 shares = Rp 2,500,000

→ BCA balance: Rp 1,023,170 → Rp 3,523,170
→ DividendRecord: BBCA, Rp 2,500,000
→ Transaction: INCOME to BCA, category="Dividen", source=PORTFOLIO
```

#### 9. Bayar Utang Cicilan
```
Date: 5 Feb 2026
Action: PAY DEBT
Debt: Utang Ahmad
Amount: Rp 500,000
From Account: BCA

→ BCA balance: Rp 3,523,170 → Rp 3,023,170
→ Debt Ahmad: remaining Rp 2,000,000 → Rp 1,500,000
→ Payment record logged
→ Transaction: EXPENSE from BCA, category="Bayar Utang", source=DEBT
```

#### 10. Terima Piutang
```
Date: 10 Mar 2026
Action: RECEIVE PAYMENT
Receivable: Piutang Budi
Amount: Rp 1,000,000
To Account: GoPay

→ GoPay balance: Rp 1,000,000 → Rp 2,000,000
→ Piutang Budi: remaining Rp 3,000,000 → Rp 2,000,000
→ Transaction: INCOME to GoPay, category="Terima Piutang", source=DEBT
```

#### 11. Jual Saham (Portfolio → Account)
```
Date: 15 Mar 2026
Action: SELL
Asset: BBCA
Qty: 50 lot @ Rp 10,500 (naik!)
Total: Rp 5,250,000 - fee Rp 5,000 = Rp 5,245,000

→ BCA balance: Rp 3,023,170 → Rp 8,268,170
→ Holding BBCA: qty 100 → 50
→ Capital gain: (10,500 - 9,000) × 5,000 = Rp 7,500,000
→ Transaction: TRANSFER to BCA, source=PORTFOLIO
→ Transaction: INCOME, category="Capital Gain", source=PORTFOLIO (jika di-track)
```

## Dashboard Unified View

```
╔══════════════════════════════════════════════╗
║            CUANGX FINANCE                    ║
╠══════════════════════════════════════════════╣
║                                              ║
║  💰 TOTAL NET WORTH          Rp 47,500,000  ║
║     ┌────────────────────────────────────┐   ║
║     │ Cash & Bank      Rp  8,268,170    │   ║
║     │ Investments       Rp 17,290,830    │   ║
║     │ Piutang           Rp  2,000,000    │   ║
║     │ Utang             Rp -1,500,000    │   ║
║     │ ──────────────────────────────     │   ║
║     │ NET               Rp 26,059,000    │   ║
║     └────────────────────────────────────┘   ║
║                                              ║
║  ⚠️ JATUH TEMPO DEKAT                       ║
║     • Utang Ahmad    Rp 1.5jt  3 hari lagi  ║
║     • Piutang Budi   Rp 2jt    2 bulan lagi ║
║                                              ║
║  📊 THIS MONTH                              ║
║     Income:    Rp 10,000,000 (gaji+dividen)  ║
║     Expense:   Rp  5,500,000 (belanja+cicilan)║
║     Invest:    Rp  1,571,830 (beli gold)     ║
║     Utang:     Rp  2,000,000 (pinjam baru)   ║
║     Piutang:   Rp -3,000,000 (kasih pinjam)  ║
║                                              ║
║  📈 PORTFOLIO TODAY        +Rp 500,000 ↑    ║
║     BBCA     +5.0%  ████████░░  Rp 5,250,000║
║     Gold     +2.1%  ████░░░░░░  Rp 1,604,000║
║                                              ║
║  📝 RECENT TRANSACTIONS                      ║
║     Today  Makan         -Rp 50,000  (GoPay) ║
║     Today  BBCA Dividen  +Rp 2.5jt  (BCA)    ║
║     Yday   Beli Gold     -Rp 1.57jt (BCA)    ║
║     Yday   Bayar utang   -Rp 500k   (BCA)    ║
║                                              ║
╚══════════════════════════════════════════════╝
```

## Account Ledger (Semua Modul Terlihat)

```
BCA Account Detail:
┌─────────────────────────────────────────────────┐
│ Balance: Rp 8,268,170                           │
├─────────────────────────────────────────────────┤
│ 1 Jan  Gaji                    +Rp 10,000,000  │
│ 2 Jan  Cicilan Rumah           -Rp  2,500,000  │
│ 5 Jan  Pinjam dari Ahmad       +Rp  2,000,000  │  ← Utang
│ 5 Jan  Makan                   -Rp     50,000  │
│10 Jan  Pinjamkan ke Budi       -Rp  3,000,000  │  ← Piutang
│15 Jan  Beli BBCA               -Rp    905,000  │  ← Portfolio
│20 Jan  Beli Gold 1gr           -Rp  1,571,830  │  ← Portfolio
│ 5 Feb  Bayar utang Ahmad       -Rp    500,000  │  ← Utang
│25 Feb  Dividen BBCA            +Rp  2,500,000  │  ← Portfolio
│15 Mar  Jual BBCA 50 lot        +Rp  5,245,000  │  ← Portfolio
└─────────────────────────────────────────────────┘
```

## Summary: Integrasi per Modul

| Modul | Action | Account Effect | Transaction Source |
|---|---|---|---|
| Expense | INCOME (gaji, bonus) | balance += amount | EXPENSE |
| Expense | EXPENSE (belanja, cicilan) | balance -= amount | EXPENSE |
| Expense | TRANSFER | from -= amount, to += amount | EXPENSE |
| Portfolio | BUY holding | balance -= (qty × price + fee) | PORTFOLIO |
| Portfolio | SELL holding | balance += (qty × price - fee) | PORTFOLIO |
| Portfolio | DIVIDEND | balance += amount | PORTFOLIO |
| Utang | BORROW | balance += amount | DEBT |
| Utang | PAY DEBT | balance -= amount | DEBT |
| Piutang | LEND | balance -= amount | DEBT |
| Piutang | RECEIVE | balance += amount | DEBT |
