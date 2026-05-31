package com.cuangx.finance.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cuangx.finance.core.database.entity.JournalEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {

    @Query("SELECT * FROM journal_entries ORDER BY date DESC, createdAt DESC")
    fun getAll(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE ticker = :ticker ORDER BY date ASC")
    fun getByTicker(ticker: String): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE action = :action ORDER BY date DESC")
    fun getByAction(action: String): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE accountId = :accountId ORDER BY date DESC")
    fun getByAccountId(accountId: Long): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    fun getById(id: Long): Flow<JournalEntryEntity?>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getByIdOnce(id: Long): JournalEntryEntity?

    @Query("SELECT * FROM journal_entries WHERE ticker = :ticker ORDER BY date ASC")
    suspend fun getByTickerOnce(ticker: String): List<JournalEntryEntity>

    @Query("SELECT DISTINCT ticker FROM journal_entries WHERE ticker IS NOT NULL")
    suspend fun getAllTickers(): List<String>

    @Query("""
        SELECT * FROM journal_entries 
        WHERE action IN ('BUY', 'SELL') 
        GROUP BY ticker 
        HAVING SUM(CASE WHEN action = 'BUY' THEN quantity ELSE -quantity END) > 0
        ORDER BY date DESC
    """)
    fun getActiveHoldings(): Flow<List<JournalEntryEntity>>

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN action = 'BUY' THEN quantity ELSE -quantity END), 0) 
        FROM journal_entries WHERE ticker = :ticker
    """)
    suspend fun getCurrentQuantity(ticker: String): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntryEntity): Long

    @Update
    suspend fun update(entry: JournalEntryEntity)

    @Delete
    suspend fun delete(entry: JournalEntryEntity)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM journal_entries")
    suspend fun getCount(): Int
}
