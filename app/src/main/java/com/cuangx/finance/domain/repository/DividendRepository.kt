package com.cuangx.finance.domain.repository

import com.cuangx.finance.domain.model.DividendRecord
import kotlinx.coroutines.flow.Flow

interface DividendRepository {
    fun getByHoldingId(holdingId: Long): Flow<List<DividendRecord>>
    fun getAll(): Flow<List<DividendRecord>>
    suspend fun getById(id: Long): DividendRecord?
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<DividendRecord>>
    fun getTotalByDateRange(startDate: Long, endDate: Long): Flow<Double>
    suspend fun insert(record: DividendRecord): Long
    suspend fun delete(record: DividendRecord)
    suspend fun deleteById(id: Long)
}
