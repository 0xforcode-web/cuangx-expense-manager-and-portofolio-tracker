package com.cuangx.finance.data.repository

import com.cuangx.finance.core.database.dao.DebtReceivableDao
import com.cuangx.finance.core.database.mapper.toDomain
import com.cuangx.finance.core.database.mapper.toEntity
import com.cuangx.finance.domain.model.DebtReceivable
import com.cuangx.finance.domain.model.DebtReceivableType
import com.cuangx.finance.domain.model.DebtStatus
import com.cuangx.finance.domain.repository.DebtReceivableRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebtReceivableRepositoryImpl @Inject constructor(
    private val debtReceivableDao: DebtReceivableDao
) : DebtReceivableRepository {

    override fun getAll(): Flow<List<DebtReceivable>> {
        return debtReceivableDao.getAll().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getByType(type: DebtReceivableType): Flow<List<DebtReceivable>> {
        return debtReceivableDao.getByType(type.name).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getAllActive(): Flow<List<DebtReceivable>> {
        return debtReceivableDao.getAllActive().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getById(id: Long): Flow<DebtReceivable?> {
        return debtReceivableDao.getById(id).map { it?.toDomain() }
    }

    override suspend fun getByIdOnce(id: Long): DebtReceivable? {
        return debtReceivableDao.getByIdOnce(id)?.toDomain()
    }

    override fun getUpcomingDue(now: Long, futureDate: Long): Flow<List<DebtReceivable>> {
        return debtReceivableDao.getUpcomingDue(now, futureDate).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getOverdue(now: Long): Flow<List<DebtReceivable>> {
        return debtReceivableDao.getOverdue(now).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTotalRemainingByType(type: DebtReceivableType): Flow<Double> {
        return debtReceivableDao.getTotalRemainingByType(type.name)
    }

    override suspend fun updateRemaining(id: Long, remaining: Double, status: DebtStatus) {
        debtReceivableDao.updateRemaining(id, remaining, status.name)
    }

    override suspend fun markOverdue(now: Long) {
        debtReceivableDao.markOverdue(now)
    }

    override suspend fun insert(debt: DebtReceivable): Long {
        return debtReceivableDao.insert(debt.toEntity())
    }

    override suspend fun update(debt: DebtReceivable) {
        debtReceivableDao.update(debt.toEntity())
    }

    override suspend fun delete(debt: DebtReceivable) {
        debtReceivableDao.delete(debt.toEntity())
    }

    override suspend fun deleteById(id: Long) {
        debtReceivableDao.deleteById(id)
    }
}
