package com.cuangx.finance.data.repository

import com.cuangx.finance.core.database.dao.RecurringTransactionDao
import com.cuangx.finance.core.database.mapper.toDomain
import com.cuangx.finance.core.database.mapper.toEntity
import com.cuangx.finance.domain.model.RecurringTransaction
import com.cuangx.finance.domain.repository.RecurringRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecurringRepositoryImpl @Inject constructor(
    private val recurringTransactionDao: RecurringTransactionDao
) : RecurringRepository {

    override fun getAll(): Flow<List<RecurringTransaction>> {
        return recurringTransactionDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllActive(): Flow<List<RecurringTransaction>> {
        return recurringTransactionDao.getAllActive().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getById(id: Long): Flow<RecurringTransaction?> {
        return recurringTransactionDao.getById(id).map { it?.toDomain() }
    }

    override suspend fun getByIdOnce(id: Long): RecurringTransaction? {
        return recurringTransactionDao.getByIdOnce(id)?.toDomain()
    }

    override suspend fun getDueRecurring(date: Long): List<RecurringTransaction> {
        return recurringTransactionDao.getDueRecurring(date).map { it.toDomain() }
    }

    override suspend fun updateNextDate(id: Long, nextDate: Long) {
        recurringTransactionDao.updateNextDate(id, nextDate)
    }

    override suspend fun updateActiveStatus(id: Long, isActive: Boolean) {
        recurringTransactionDao.updateActiveStatus(id, isActive)
    }

    override suspend fun insert(recurring: RecurringTransaction): Long {
        return recurringTransactionDao.insert(recurring.toEntity())
    }

    override suspend fun update(recurring: RecurringTransaction) {
        recurringTransactionDao.update(recurring.toEntity())
    }

    override suspend fun delete(recurring: RecurringTransaction) {
        recurringTransactionDao.delete(recurring.toEntity())
    }

    override suspend fun deleteById(id: Long) {
        recurringTransactionDao.deleteById(id)
    }
}
