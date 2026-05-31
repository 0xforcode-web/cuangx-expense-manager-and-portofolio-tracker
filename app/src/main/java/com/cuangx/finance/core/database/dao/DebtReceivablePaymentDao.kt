package com.cuangx.finance.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cuangx.finance.core.database.entity.DebtReceivablePaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtReceivablePaymentDao {

    @Query("SELECT * FROM debt_receivable_payments WHERE debtId = :debtId ORDER BY date DESC")
    fun getByDebtId(debtId: Long): Flow<List<DebtReceivablePaymentEntity>>

    @Query("SELECT * FROM debt_receivable_payments ORDER BY date DESC")
    fun getAll(): Flow<List<DebtReceivablePaymentEntity>>

    @Query("SELECT * FROM debt_receivable_payments WHERE id = :id")
    suspend fun getById(id: Long): DebtReceivablePaymentEntity?

    @Query("SELECT COALESCE(SUM(amount), 0) FROM debt_receivable_payments WHERE debtId = :debtId")
    suspend fun getTotalPaidByDebtId(debtId: Long): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: DebtReceivablePaymentEntity): Long

    @Query("UPDATE debt_receivable_payments SET transactionId = :transactionId WHERE id = :id")
    suspend fun updateTransactionId(id: Long, transactionId: Long)

    @Delete
    suspend fun delete(payment: DebtReceivablePaymentEntity)

    @Query("DELETE FROM debt_receivable_payments WHERE id = :id")
    suspend fun deleteById(id: Long)
}
