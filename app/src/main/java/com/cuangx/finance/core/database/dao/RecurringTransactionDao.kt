package com.cuangx.finance.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cuangx.finance.core.database.entity.RecurringTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringTransactionDao {

    @Query("SELECT * FROM recurring_transactions ORDER BY nextDate ASC")
    fun getAll(): Flow<List<RecurringTransactionEntity>>

    @Query("SELECT * FROM recurring_transactions WHERE isActive = 1 ORDER BY nextDate ASC")
    fun getAllActive(): Flow<List<RecurringTransactionEntity>>

    @Query("SELECT * FROM recurring_transactions WHERE id = :id")
    fun getById(id: Long): Flow<RecurringTransactionEntity?>

    @Query("SELECT * FROM recurring_transactions WHERE id = :id")
    suspend fun getByIdOnce(id: Long): RecurringTransactionEntity?

    @Query("SELECT * FROM recurring_transactions WHERE isActive = 1 AND nextDate <= :date ORDER BY nextDate ASC")
    suspend fun getDueRecurring(date: Long): List<RecurringTransactionEntity>

    @Query("UPDATE recurring_transactions SET nextDate = :nextDate WHERE id = :id")
    suspend fun updateNextDate(id: Long, nextDate: Long)

    @Query("UPDATE recurring_transactions SET isActive = :isActive WHERE id = :id")
    suspend fun updateActiveStatus(id: Long, isActive: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recurring: RecurringTransactionEntity): Long

    @Update
    suspend fun update(recurring: RecurringTransactionEntity)

    @Delete
    suspend fun delete(recurring: RecurringTransactionEntity)

    @Query("DELETE FROM recurring_transactions WHERE id = :id")
    suspend fun deleteById(id: Long)
}
