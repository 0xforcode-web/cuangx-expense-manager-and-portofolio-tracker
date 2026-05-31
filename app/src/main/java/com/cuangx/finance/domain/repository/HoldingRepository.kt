package com.cuangx.finance.domain.repository

import com.cuangx.finance.domain.model.Holding
import kotlinx.coroutines.flow.Flow

interface HoldingRepository {
    fun getAll(): Flow<List<Holding>>
    fun getByAssetType(assetType: String): Flow<List<Holding>>
    fun getById(id: Long): Flow<Holding?>
    suspend fun getByIdOnce(id: Long): Holding?
    suspend fun getAllTickers(): List<String>
    suspend fun getByTicker(ticker: String): Holding?
    suspend fun insert(holding: Holding): Long
    suspend fun update(holding: Holding)
    suspend fun delete(holding: Holding)
    suspend fun deleteById(id: Long)
    suspend fun getCount(): Int
}
