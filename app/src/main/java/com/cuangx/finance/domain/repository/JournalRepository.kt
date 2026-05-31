package com.cuangx.finance.domain.repository

import com.cuangx.finance.domain.model.JournalEntry
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun getAll(): Flow<List<JournalEntry>>
    fun getByTicker(ticker: String): Flow<List<JournalEntry>>
    fun getByAction(action: String): Flow<List<JournalEntry>>
    fun getByAccountId(accountId: Long): Flow<List<JournalEntry>>
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<JournalEntry>>
    fun getById(id: Long): Flow<JournalEntry?>
    suspend fun getByIdOnce(id: Long): JournalEntry?
    suspend fun getByTickerOnce(ticker: String): List<JournalEntry>
    suspend fun getAllTickers(): List<String>
    suspend fun getCurrentQuantity(ticker: String): Double
    suspend fun insert(entry: JournalEntry): Long
    suspend fun update(entry: JournalEntry)
    suspend fun delete(entry: JournalEntry)
    suspend fun deleteById(id: Long)
    suspend fun getCount(): Int
}
