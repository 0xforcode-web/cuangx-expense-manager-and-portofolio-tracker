# CuangX Finance - Feature: Portfolio Tracker

## Overview

Modul Portfolio Tracker menangani tracking investasi: saham, ETF, crypto, Logam Mulia, real estate, dan aset lainnya. Terintegrasi penuh dengan Account — setiap buy/sell/dividend otomatis mempengaruhi saldo akun.

## Supported Asset Types

| Type | Ticker Format | Price Source | Currency |
|---|---|---|---|
| STOCK | BBCA.JK, AAPL | Yahoo Finance | IDR/USD |
| ETF | SPY, IVV | Yahoo Finance | USD |
| CRYPTO | BTC-USD, ETH-USD | Yahoo Finance | USD |
| GOLD | GC=F | Yahoo Finance | USD (convert to IDR/gram) |
| REAL_ESTATE | null | Manual input | IDR |
| ART | null | Manual input | IDR |
| COLLECTIBLE | null | Manual input | IDR |
| OTHER | null | Manual input | IDR |

## Features

### 1. Holdings

**Data per Holding:**
- Asset type & ticker
- Name (auto-filled from API if ticker exists)
- Quantity (shares, units, grams)
- Average buy price (auto-calculated from buy transactions)
- Current market value (quantity × current price)
- P&L (current value - total cost)
- P&L %

**Screens:**
- **PortfolioOverviewScreen**
  - Total portfolio value di atas
  - Daily P&L (amount + %)
  - Pie chart by asset type
  - List holdings dengan: name, ticker, qty, current price, P&L, P&L %

- **AddEditHoldingScreen**
  - Asset type picker
  - Ticker input (with search autocomplete for stocks/ETF/crypto)
  - Name input (auto-fill from search result)
  - Quantity input
  - Buy price input (for first buy)
  - Funding account picker (which account paid for this)
  - Note

- **HoldingDetailScreen**
  - Header: name, ticker, asset type
  - Current price + daily change
  - P&L summary
  - Price chart (historical, if available)
  - Transaction history (buy/sell)
  - Dividend history
  - Actions: Buy More, Sell, Add Dividend

### 2. Portfolio Transactions (Buy/Sell)

**Buy Flow:**
```
User enters: quantity, price per unit, fee, account
→ Total cost = (quantity × price) + fee
→ Check: account.balance >= total cost?
  → YES: 
    1. account.balance -= total cost
    2. Create Transaction(TRANSFER, from=account, source=PORTFOLIO)
    3. Update holding: quantity += qty, recalculate avgBuyPrice
  → NO: Show error "Saldo tidak cukup"
```

**Sell Flow:**
```
User enters: quantity, price per unit, fee, account
→ Total received = (quantity × price) - fee
→ Check: holding.quantity >= quantity?
  → YES:
    1. account.balance += total received
    2. Create Transaction(TRANSFER, to=account, source=PORTFOLIO)
    3. Capital gain = (sellPrice - avgBuyPrice) × quantity
    4. If capital gain > 0: Create Transaction(INCOME, category="Capital Gain")
    5. Update holding: quantity -= qty
  → NO: Show error "Jumlah aset tidak cukup"
```

**Average Buy Price Calculation:**
```
Old: 100 shares @ Rp 10,000 = Rp 1,000,000
Buy: 50 shares @ Rp 12,000 = Rp 600,000
New avg = (1,000,000 + 600,000) / (100 + 50) = Rp 10,667
New qty = 150
```

### 3. Dividends

**Dividend Flow:**
```
User enters: holding, amount, destination account, date
→ 1. account.balance += amount
→ 2. Create Transaction(INCOME, category="Dividen", to=account, source=PORTFOLIO)
→ 3. Create DividendRecord
```

**Screens:**
- **DividendScreen**
  - Calendar view with dividend dates marked
  - List: this year's dividends by holding
  - Summary: total received, monthly breakdown
  - Forecast: expected dividends from holdings with known dividend schedules

### 4. Logam Mulia (Gold)

**Special Handling:**
- Asset type: GOLD
- Ticker: GC=F (Gold futures)
- Quantity unit: gram
- Price: auto-calculated per gram in IDR

**Price Calculation:**
```kotlin
// Fetch from Yahoo Finance
val goldUsdPerOz = yahooFinanceApi.getQuote("GC=F").chart.result[0].meta.regularMarketPrice
val usdIdrRate = yahooFinanceApi.getQuote("USDIDR=X").chart.result[0].meta.regularMarketPrice

// Calculate
val pricePerGramIdr = GoldCalculator.calculatePricePerGram(goldUsdPerOz, usdIdrRate)
// = (goldUsdPerOz / 31.1035) * usdIdrRate
```

**Display:**
```
Logam Mulia
  Qty: 10 gram
  Avg Buy: Rp 1,500,000/gram
  Current: Rp 1,571,830/gram
  P&L: +Rp 718,300 (+4.8%)
  Value: Rp 15,718,300
```

### 5. Portfolio Analysis

**Breakdowns:**
| Dimension | Chart | Example |
|---|---|---|
| Asset Type | Pie chart | Stocks 60%, Gold 25%, Crypto 15% |
| Region | Pie chart | Indonesia 70%, US 30% |
| Industry | Bar chart | Banking 40%, Tech 30%, Mining 20% |

**Metrics:**
| Metric | Formula |
|---|---|
| Total Value | sum(qty × currentPrice) |
| Total Cost | sum(qty × avgBuyPrice) |
| Unrealized P&L | totalValue - totalCost |
| Unrealized P&L % | (unrealizedPnl / totalCost) × 100 |
| Daily P&L | sum(qty × (currentPrice - previousClose)) |
| Time-Weighted Return | (complex calculation based on cash flows) |

**Screens:**
- **AnalysisScreen**
  - Toggle: By Type / By Region / By Industry
  - Pie chart + legend with percentages
  - List below chart with details
  - Metrics cards at top

### 6. Net Worth

**Calculation:**
```kotlin
fun calculateNetWorth(): Flow<Double> {
    return combine(
        accountRepository.getTotalBalance(),
        holdingRepository.getTotalPortfolioValue(),
        debtReceivableRepository.getTotalDebt(),
        debtReceivableRepository.getTotalReceivable()
    ) { accounts, portfolio, debt, receivable ->
        accounts + portfolio + receivable - debt
    }
}
```

**Screens:**
- **NetWorthScreen**
  - Line chart: net worth over time (monthly snapshots)
  - Current net worth breakdown:
    - Cash & Bank: Rp xxx
    - Investments: Rp xxx
    - Piutang: Rp xxx
    - Utang: -Rp xxx
    - NET: Rp xxx
  - Monthly change: +/- amount and %

### 7. Price Refresh

**Repository:**
```kotlin
class PriceRepositoryImpl @Inject constructor(
    private val yahooFinanceApi: YahooFinanceApi,
    private val swissquoteApi: SwissquoteApi,
    private val priceCacheDao: PriceCacheDao
) : PriceRepository {

    override suspend fun refreshPrices(tickers: List<String>): ApiResult<Unit> {
        return safeApiCall {
            for (ticker in tickers) {
                val price = fetchPrice(ticker)
                priceCacheDao.insert(price)
            }
        }
    }

    private suspend fun fetchPrice(ticker: String): PriceCacheEntity {
        return if (ticker == "GOLD_GRAM_IDR") {
            // Special: fetch GC=F + USDIDR=X, calculate per gram
            val gold = yahooFinanceApi.getQuote("GC=F")
            val usdIdr = yahooFinanceApi.getQuote("USDIDR=X")
            val pricePerGram = GoldCalculator.calculatePricePerGram(
                gold.chart.result[0].meta.regularMarketPrice,
                usdIdr.chart.result[0].meta.regularMarketPrice
            )
            PriceCacheEntity(
                ticker = "GOLD_GRAM_IDR",
                price = pricePerGram,
                currency = "IDR",
                name = "Logam Mulia",
                changePercent = 0.0, // calculate from gold change
                lastUpdated = System.currentTimeMillis()
            )
        } else if (ticker.endsWith(".JK") || !ticker.contains("-")) {
            // Yahoo Finance for stocks/ETFs
            val response = yahooFinanceApi.getQuote(ticker)
            val meta = response.chart.result[0].meta
            val changePercent = ((meta.regularMarketPrice - meta.chartPreviousClose) / meta.chartPreviousClose) * 100
            PriceCacheEntity(
                ticker = ticker,
                price = meta.regularMarketPrice,
                currency = meta.currency,
                name = meta.shortName ?: ticker,
                changePercent = changePercent,
                lastUpdated = System.currentTimeMillis()
            )
        } else {
            // Yahoo Finance for crypto (BTC-USD, ETH-USD)
            val response = yahooFinanceApi.getQuote(ticker)
            val meta = response.chart.result[0].meta
            PriceCacheEntity(
                ticker = ticker,
                price = meta.regularMarketPrice,
                currency = meta.currency,
                name = meta.shortName ?: ticker,
                changePercent = 0.0,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }
}
```

### 8. Price Refresh Worker

```kotlin
class PriceRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val holdingRepository: HoldingRepository,
    private val priceRepository: PriceRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val tickers = holdingRepository.getAllTickers()
        if (tickers.isEmpty()) return Result.success()

        val result = priceRepository.refreshPrices(tickers)
        return when (result) {
            is ApiResult.Success -> Result.success()
            is ApiResult.Error -> Result.retry()
            is ApiResult.Loading -> Result.success()
        }
    }
}
```

## Integration with Account

| Event | Account Effect | Transaction Created |
|---|---|---|
| BUY holding | balance -= (qty × price + fee) | TRANSFER from account, source=PORTFOLIO |
| SELL holding | balance += (qty × price - fee) | TRANSFER to account, source=PORTFOLIO |
| SELL with profit | (same as above) | + INCOME for capital gain |
| DIVIDEND received | balance += amount | INCOME to account, category="Dividen" |

## File Summary

| File | Type | Description |
|---|---|---|
| HoldingEntity.kt | Entity | Room entity |
| HoldingDao.kt | DAO | Database access |
| HoldingRepository.kt | Repository | Business logic |
| PortfolioOverviewScreen.kt | Screen | Overview UI |
| PortfolioOverviewViewModel.kt | ViewModel | Overview logic |
| HoldingsList.kt | Component | Holdings list composable |
| PortfolioPieChart.kt | Component | Pie chart composable |
| DailyPnlCard.kt | Component | P&L card composable |
| HoldingDetailScreen.kt | Screen | Detail UI |
| HoldingDetailViewModel.kt | ViewModel | Detail logic |
| AddEditHoldingScreen.kt | Screen | Form UI |
| AddEditHoldingViewModel.kt | ViewModel | Form logic |
| PortfolioTransactionEntity.kt | Entity | Room entity |
| PortfolioTransactionDao.kt | DAO | Database access |
| PortfolioTransactionRepository.kt | Repository | Business logic |
| DividendRecordEntity.kt | Entity | Room entity |
| DividendRecordDao.kt | DAO | Database access |
| DividendRepository.kt | Repository | Business logic |
| DividendScreen.kt | Screen | Dividend UI |
| DividendViewModel.kt | ViewModel | Dividend logic |
| AnalysisScreen.kt | Screen | Analysis UI |
| AnalysisViewModel.kt | ViewModel | Analysis logic |
| NetWorthScreen.kt | Screen | Net worth UI |
| NetWorthViewModel.kt | ViewModel | Net worth logic |
| PriceCacheEntity.kt | Entity | Room entity |
| PriceCacheDao.kt | DAO | Database access |
| PriceRepository.kt | Repository | Price fetching logic |
| YahooFinanceApi.kt | API | Retrofit interface |
| YahooFinanceModels.kt | API | Response DTOs |
| SwissquoteApi.kt | API | Retrofit interface |
| SwissquoteModels.kt | API | Response DTOs |
| GoldCalculator.kt | Utility | Gold price formula |
| PriceRefreshWorker.kt | Worker | Background price refresh |
