package com.cuangx.finance.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cuangx.finance.core.database.entity.DebtReceivableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtReceivableDao {

    @Query("SELECT * FROM debts_receivables ORDER BY status ASC, dueDate ASC")
    fun getAll(): Flow<List<DebtReceivableEntity>>

    @Query("SELECT * FROM debts_receivables WHERE type = :type ORDER BY status ASC, dueDate ASC")
    fun getByType(type: String): Flow<List<DebtReceivableEntity>>

    @Query("SELECT * FROM debts_receivables WHERE status = 'ACTIVE' ORDER BY dueDate ASC")
    fun getAllActive(): Flow<List<DebtReceivableEntity>>

    @Query("SELECT * FROM debts_receivables WHERE id = :id")
    fun getById(id: Long): Flow<DebtReceivableEntity?>

    @Query("SELECT * FROM debts_receivables WHERE id = :id")
    suspend fun getByIdOnce(id: Long): DebtReceivableEntity?

    @Query("""
        SELECT * FROM debts_receivables 
        WHERE status = 'ACTIVE' AND dueDate IS NOT NULL AND dueDate BETWEEN :now AND :futureDate 
        ORDER BY dueDate ASC
    """)
    fun getUpcomingDue(now: Long, futureDate: Long): Flow<List<DebtReceivableEntity>>

    @Query("""
        SELECT * FROM debts_receivables 
        WHERE status = 'ACTIVE' AND dueDate IS NOT NULL AND dueDate < :now
    """)
    fun getOverdue(now: Long): Flow<List<DebtReceivableEntity>>

    @Query("SELECT COALESCE(SUM(remainingAmount), 0) FROM debts_receivables WHERE type = :type AND status = 'ACTIVE'")
    fun getTotalRemainingByType(type: String): Flow<Double>

    @Query("UPDATE debts_receivables SET remainingAmount = :remaining, status = :status WHERE id = :id")
    suspend fun updateRemaining(id: Long, remaining: Double, status: String)

    @Query("""
        UPDATE debts_receivables SET status = 'OVERDUE' 
        WHERE status = 'ACTIVE' AND dueDate IS NOT NULL AND dueDate < :now
    """)
    suspend fun markOverdue(now: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(debt: DebtReceivableEntity): Long

    @Update
    suspend fun update(debt: DebtReceivableEntity)

    @Delete
    suspend fun delete(debt: DebtReceivableEntity)

    @Query("DELETE FROM debts_receivables WHERE id = :id")
    suspend fun deleteById(id: Long)
}
