package com.cuangx.finance.domain.repository

import com.cuangx.finance.domain.model.DebtReceivable
import com.cuangx.finance.domain.model.DebtReceivableType
import com.cuangx.finance.domain.model.DebtStatus
import kotlinx.coroutines.flow.Flow

interface DebtReceivableRepository {
    fun getAll(): Flow<List<DebtReceivable>>
    fun getByType(type: DebtReceivableType): Flow<List<DebtReceivable>>
    fun getAllActive(): Flow<List<DebtReceivable>>
    fun getById(id: Long): Flow<DebtReceivable?>
    suspend fun getByIdOnce(id: Long): DebtReceivable?
    fun getUpcomingDue(now: Long, futureDate: Long): Flow<List<DebtReceivable>>
    fun getOverdue(now: Long): Flow<List<DebtReceivable>>
    fun getTotalRemainingByType(type: DebtReceivableType): Flow<Double>
    suspend fun updateRemaining(id: Long, remaining: Double, status: DebtStatus)
    suspend fun markOverdue(now: Long)
    suspend fun insert(debt: DebtReceivable): Long
    suspend fun update(debt: DebtReceivable)
    suspend fun delete(debt: DebtReceivable)
    suspend fun deleteById(id: Long)
}
