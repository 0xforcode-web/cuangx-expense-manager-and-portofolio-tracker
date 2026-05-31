package com.cuangx.finance.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cuangx.finance.core.database.entity.PriceCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceCacheDao {

    @Query("SELECT * FROM price_cache WHERE ticker = :ticker")
    suspend fun getByTicker(ticker: String): PriceCacheEntity?

    @Query("SELECT * FROM price_cache WHERE ticker = :ticker")
    fun observeByTicker(ticker: String): Flow<PriceCacheEntity?>

    @Query("SELECT * FROM price_cache")
    fun getAll(): Flow<List<PriceCacheEntity>>

    @Query("SELECT * FROM price_cache WHERE ticker IN (:tickers)")
    suspend fun getByTickers(tickers: List<String>): List<PriceCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(price: PriceCacheEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prices: List<PriceCacheEntity>)

    @Query("DELETE FROM price_cache WHERE ticker = :ticker")
    suspend fun deleteByTicker(ticker: String)

    @Query("DELETE FROM price_cache")
    suspend fun deleteAll()

    @Query("SELECT lastUpdated FROM price_cache WHERE ticker = :ticker")
    suspend fun getLastUpdated(ticker: String): Long?
}
