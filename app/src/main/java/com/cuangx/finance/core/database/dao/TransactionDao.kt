package com.cuangx.finance.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cuangx.finance.core.database.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, createdAt DESC")
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY date DESC, createdAt DESC")
    fun getByAccountId(accountId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC, createdAt DESC")
    fun getByCategoryId(categoryId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC, createdAt DESC")
    fun getByType(type: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getById(id: Long): Flow<TransactionEntity?>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getByIdOnce(id: Long): TransactionEntity?

    @Query("""
        SELECT COALESCE(SUM(amount), 0) FROM transactions 
        WHERE categoryId = :categoryId AND type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate
    """)
    suspend fun getExpenseByCategoryAndDateRange(categoryId: Long, startDate: Long, endDate: Long): Double

    @Query("""
        SELECT COALESCE(SUM(amount), 0) FROM transactions 
        WHERE type = 'INCOME' AND date BETWEEN :startDate AND :endDate
    """)
    fun getTotalIncomeByDateRange(startDate: Long, endDate: Long): Flow<Double>

    @Query("""
        SELECT COALESCE(SUM(amount), 0) FROM transactions 
        WHERE type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate
    """)
    fun getTotalExpenseByDateRange(startDate: Long, endDate: Long): Flow<Double>

    @Query("SELECT * FROM transactions WHERE isBookmarked = 1 ORDER BY date DESC")
    fun getBookmarked(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getCount(): Int
}
