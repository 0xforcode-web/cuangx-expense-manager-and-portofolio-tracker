package com.cuangx.finance.domain.repository

import com.cuangx.finance.domain.model.RecurringTransaction
import kotlinx.coroutines.flow.Flow

interface RecurringRepository {
    fun getAll(): Flow<List<RecurringTransaction>>
    fun getAllActive(): Flow<List<RecurringTransaction>>
    fun getById(id: Long): Flow<RecurringTransaction?>
    suspend fun getByIdOnce(id: Long): RecurringTransaction?
    suspend fun getDueRecurring(date: Long): List<RecurringTransaction>
    suspend fun updateNextDate(id: Long, nextDate: Long)
    suspend fun updateActiveStatus(id: Long, isActive: Boolean)
    suspend fun insert(recurring: RecurringTransaction): Long
    suspend fun update(recurring: RecurringTransaction)
    suspend fun delete(recurring: RecurringTransaction)
    suspend fun deleteById(id: Long)
}
