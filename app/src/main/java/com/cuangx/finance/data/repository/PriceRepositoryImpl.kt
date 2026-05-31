package com.cuangx.finance.data.repository

import com.cuangx.finance.core.database.dao.PriceCacheDao
import com.cuangx.finance.core.database.mapper.toDomain
import com.cuangx.finance.core.database.mapper.toEntity
import com.cuangx.finance.core.network.yahoo.YahooFinanceApi
import com.cuangx.finance.core.util.GoldCalculator
import com.cuangx.finance.domain.model.PriceData
import com.cuangx.finance.domain.repository.PriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceRepositoryImpl @Inject constructor(
    private val yahooFinanceApi: YahooFinanceApi,
    private val priceCacheDao: PriceCacheDao
) : PriceRepository {

    companion object {
        private const val CACHE_TTL_MS = 15 * 60 * 1000L // 15 minutes
    }

    override suspend fun getPrice(ticker: String): PriceData? {
        val cached = priceCacheDao.getByTicker(ticker)
        if (cached != null) {
            val age = System.currentTimeMillis() - cached.lastUpdated
            if (age < CACHE_TTL_MS) {
                return cached.toDomain()
            }
        }

        return try {
            val response = yahooFinanceApi.getQuote(ticker)
            val meta = response.chart.result?.firstOrNull()?.meta ?: return null
            val price = meta.regularMarketPrice ?: return null
            val previousClose = meta.chartPreviousClose ?: price
            val changePercent = if (previousClose > 0) ((price - previousClose) / previousClose) * 100 else 0.0

            val priceData = PriceData(
                ticker = ticker,
                price = price,
                currency = meta.currency ?: "USD",
                name = meta.shortName ?: ticker,
                changePercent = changePercent,
                lastUpdated = System.currentTimeMillis()
            )

            priceCacheDao.insert(priceData.toEntity())
            priceData
        } catch (e: Exception) {
            null
        }
    }

    override fun observePrice(ticker: String): Flow<PriceData?> {
        return priceCacheDao.observeByTicker(ticker).map { it?.toDomain() }
    }

    override fun getAllCached(): Flow<List<PriceData>> {
        return priceCacheDao.getAll().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun refreshPrices(tickers: List<String>): Result<Unit> {
        return try {
            for (ticker in tickers) {
                try {
                    val response = yahooFinanceApi.getQuote(ticker)
                    val meta = response.chart.result?.firstOrNull()?.meta ?: continue
                    val price = meta.regularMarketPrice ?: continue
                    val previousClose = meta.chartPreviousClose ?: price
                    val changePercent = if (previousClose > 0) ((price - previousClose) / previousClose) * 100 else 0.0

                    val priceData = PriceData(
                        ticker = ticker,
                        price = price,
                        currency = meta.currency ?: "USD",
                        name = meta.shortName ?: ticker,
                        changePercent = changePercent,
                        lastUpdated = System.currentTimeMillis()
                    )

                    priceCacheDao.insert(priceData.toEntity())
                } catch (e: Exception) {
                    continue
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshGoldPrice(): Result<PriceData> {
        return try {
            val goldResponse = yahooFinanceApi.getQuote("GC=F")
            val goldPrice = goldResponse.chart.result?.firstOrNull()?.meta?.regularMarketPrice
                ?: return Result.failure(Exception("Failed to fetch gold price"))

            val usdIdrRate = getUsdIdrRate() ?: return Result.failure(Exception("Failed to fetch USD/IDR rate"))

            val pricePerGram = GoldCalculator.calculatePricePerGram(goldPrice, usdIdrRate)

            val priceData = PriceData(
                ticker = "GOLD_GRAM_IDR",
                price = pricePerGram,
                currency = "IDR",
                name = "Logam Mulia",
                changePercent = 0.0,
                lastUpdated = System.currentTimeMillis()
            )

            priceCacheDao.insert(priceData.toEntity())
            Result.success(priceData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUsdIdrRate(): Double? {
        return try {
            val response = yahooFinanceApi.getQuote("USDIDR=X")
            response.chart.result?.firstOrNull()?.meta?.regularMarketPrice
        } catch (e: Exception) {
            null
        }
    }
}
