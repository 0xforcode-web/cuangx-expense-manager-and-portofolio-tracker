# CuangX Finance - API Integration

## Overview

CuangX Finance menggunakan 2 API eksternal untuk data harga real-time:
1. **Yahoo Finance** — Stock, ETF, Crypto, Gold Futures, USD/IDR
2. **Swissquote** — XAU/USD (Gold spot), Forex pairs

Semua data harga di-cache di Room (`price_cache` table) dan hanya di-refresh:
- Saat user klik refresh manual
- Saat app dibuka (jika cache > 15 menit)
- Via WorkManager periodic refresh (opsional)

## Yahoo Finance API

### Base URL
```
https://query1.finance.yahoo.com
```

### Endpoints

#### Get Quote (Stock/ETF/Crypto/Gold/Forex)
```
GET /v8/finance/chart/{ticker}?interval=1d&range=1d
```

**Parameters:**
| Param | Type | Description |
|---|---|---|
| ticker | String | Symbol ticker (BBCA.JK, BTC-USD, GC=F, USDIDR=X) |
| interval | String | Data interval (1d, 1wk, 1mo) |
| range | String | Data range (1d, 5d, 1mo, 3mo, 1y) |

**Response:**
```json
{
  "chart": {
    "result": [{
      "meta": {
        "currency": "USD",
        "symbol": "GC=F",
        "regularMarketPrice": 4420.3,
        "regularMarketDayHigh": 4502.0,
        "regularMarketDayLow": 4395.6,
        "regularMarketVolume": 32625,
        "chartPreviousClose": 4481.5
      },
      "indicators": {
        "quote": [{
          "high": [4502.0],
          "low": [4395.6],
          "open": [4488.0],
          "close": [4420.3],
          "volume": [32625]
        }]
      }
    }]
  }
}
```

**Key fields to extract:**
- `meta.regularMarketPrice` → current price
- `meta.chartPreviousClose` → previous close (for change % calculation)
- `meta.currency` → price currency
- `meta.shortName` → asset name

**Common Tickers:**
| Asset | Ticker | Currency |
|---|---|---|
| BBCA (Bank BCA) | BBCA.JK | IDR |
| TLKM (Telkom) | TLKM.JK | IDR |
| AAPL (Apple) | AAPL | USD |
| BTC | BTC-USD | USD |
| ETH | ETH-USD | USD |
| Gold Futures | GC=F | USD |
| USD/IDR | USDIDR=X | IDR |

#### Search Symbol
```
GET /v6/finance/autocomplete?query={query}
```

**Response:**
```json
{
  "ResultSet": {
    "Query": "bank bca",
    "Result": [
      {"symbol": "BBCA.JK", "name": "Bank Central Asia Tbk", "typeDisp": "Equity", "exchange": "JKT"}
    ]
  }
}
```

### Implementation

```kotlin
interface YahooFinanceApi {
    @GET("v8/finance/chart/{ticker}")
    suspend fun getQuote(
        @Path("ticker") ticker: String,
        @Query("interval") interval: String = "1d",
        @Query("range") range: String = "1d"
    ): YahooChartResponse

    @GET("v6/finance/autocomplete")
    suspend fun searchSymbol(
        @Query("query") query: String
    ): YahooSearchResponse
}
```

## Swissquote API

### Base URL
```
https://forex-data-feed.swissquote.com
```

### Endpoints

#### Get Forex Quote
```
GET /public-quotes/bboquotes/instrument/{base}/{quote}
```

**Parameters:**
| Param | Type | Description |
|---|---|---|
| base | String | Base currency (XAU, EUR, GBP) |
| quote | String | Quote currency (USD, IDR) |

**Response:**
```json
[
  {
    "spreadProfilePrices": [
      {
        "spreadProfile": "premium",
        "bid": 4395.421,
        "ask": 4396.079
      }
    ]
  }
]
```

**Key fields:**
- `spreadProfilePrices[0].bid` → bid price
- `spreadProfilePrices[0].ask` → ask price
- Mid price = (bid + ask) / 2

**Common Pairs:**
| Pair | Description | Usage |
|---|---|---|
| XAU/USD | Gold spot per troy ounce | Logam Mulia calculation |
| EUR/USD | Euro to Dollar | Reference |

**Note:** USD/IDR tidak tersedia di Swissquote, gunakan Yahoo Finance (`USDIDR=X`).

### Implementation

```kotlin
interface SwissquoteApi {
    @GET("public-quotes/bboquotes/instrument/{base}/{quote}")
    suspend fun getQuote(
        @Path("base") base: String,
        @Path("quote") quote: String
    ): List<SwissquoteResponse>
}
```

## Logam Mulia (Gold per Gram in IDR)

### Formula
```
Harga per gram (IDR) = (Gold_Price_USD_per_Oz / 31.1035) * USD_IDR_Rate
```

### Variables
| Variable | Source | Ticker/Endpoint |
|---|---|---|
| Gold_Price_USD_per_Oz | Yahoo Finance | `GC=F` → `meta.regularMarketPrice` |
| USD_IDR_Rate | Yahoo Finance | `USDIDR=X` → `meta.regularMarketPrice` |
| 31.1035 | Constant | 1 troy ounce = 31.1035 grams |

### Implementation

```kotlin
object GoldCalculator {
    private const val TROY_OZ_TO_GRAM = 31.1035

    fun calculatePricePerGram(goldUsdPerOz: Double, usdIdrRate: Double): Double {
        return (goldUsdPerOz / TROY_OZ_TO_GRAM) * usdIdrRate
    }

    fun calculateTotalValue(gramQuantity: Double, pricePerGram: Double): Double {
        return gramQuantity * pricePerGram
    }
}
```

### Example Calculation
```
Gold (GC=F) = $3,350.00 / troy oz
USD/IDR = 16,450.00

Harga per gram = (3,350.00 / 31.1035) * 16,450.00
               = 107.71 * 16,450.00
               = Rp 1,771,830 / gram

User beli 5 gram:
Total value = 5 * 1,771,830 = Rp 8,859,150
```

## Price Cache Strategy

### Cache Table
```kotlin
@Entity(tableName = "price_cache")
data class PriceCacheEntity(
    @PrimaryKey val ticker: String,
    val price: Double,
    val currency: String,
    val name: String,
    val changePercent: Double,
    val lastUpdated: Long
)
```

### Refresh Logic
```
User opens app OR clicks refresh
  → Check price_cache for each ticker
  → If lastUpdated > 15 minutes ago → fetch from API
  → Update price_cache
  → Recalculate portfolio values

WorkManager (optional):
  → Periodic work every 15 minutes (when app is in background)
  → Only refreshes if app has been used in last hour
```

### API Rate Limits
| API | Limit | Strategy |
|---|---|---|
| Yahoo Finance | No official limit, but ~2000 req/hour safe | Batch requests, cache 15 min |
| Swissquote | No official limit | Cache 15 min |

## Retrofit Setup

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @YahooClient
    fun provideYahooRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://query1.finance.yahoo.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @SwissquoteClient
    fun provideSwissquoteRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://forex-data-feed.swissquote.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideYahooFinanceApi(@YahooClient retrofit: Retrofit): YahooFinanceApi {
        return retrofit.create(YahooFinanceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSwissquoteApi(@SwissquoteClient retrofit: Retrofit): SwissquoteApi {
        return retrofit.create(SwissquoteApi::class.java)
    }
}
```

## Error Handling

```kotlin
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val exception: Throwable? = null) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): ApiResult<T> {
    return try {
        ApiResult.Success(apiCall())
    } catch (e: IOException) {
        ApiResult.Error("Network error. Check your connection.", e)
    } catch (e: HttpException) {
        ApiResult.Error("Server error: ${e.code()}", e)
    } catch (e: Exception) {
        ApiResult.Error(e.message ?: "Unknown error", e)
    }
}
```
