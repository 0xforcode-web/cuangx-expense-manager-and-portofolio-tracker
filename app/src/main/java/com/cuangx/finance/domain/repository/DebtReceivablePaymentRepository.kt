package com.cuangx.finance.domain.repository

import com.cuangx.finance.domain.model.DebtReceivablePayment
import kotlinx.coroutines.flow.Flow

interface DebtReceivablePaymentRepository {
    fun getByDebtId(debtId: Long): Flow<List<DebtReceivablePayment>>
    fun getAll(): Flow<List<DebtReceivablePayment>>
    suspend fun getById(id: Long): DebtReceivablePayment?
    suspend fun getTotalPaidByDebtId(debtId: Long): Double
    suspend fun insert(payment: DebtReceivablePayment): Long
    suspend fun updateTransactionId(id: Long, transactionId: Long)
    suspend fun delete(payment: DebtReceivablePayment)
    suspend fun deleteById(id: Long)
}
