package com.cuangx.finance.domain.repository

import com.cuangx.finance.domain.model.PriceData
import kotlinx.coroutines.flow.Flow

interface PriceRepository {
    suspend fun getPrice(ticker: String): PriceData?
    fun observePrice(ticker: String): Flow<PriceData?>
    fun getAllCached(): Flow<List<PriceData>>
    suspend fun refreshPrices(tickers: List<String>): Result<Unit>
    suspend fun refreshGoldPrice(): Result<PriceData>
    suspend fun getUsdIdrRate(): Double?
}
