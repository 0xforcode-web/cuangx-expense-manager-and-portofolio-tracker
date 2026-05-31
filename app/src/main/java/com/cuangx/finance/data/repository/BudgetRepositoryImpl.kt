package com.cuangx.finance.data.repository

import com.cuangx.finance.core.database.dao.BudgetDao
import com.cuangx.finance.core.database.mapper.toDomain
import com.cuangx.finance.core.database.mapper.toEntity
import com.cuangx.finance.domain.model.Budget
import com.cuangx.finance.domain.model.BudgetPeriod
import com.cuangx.finance.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override fun getAllActive(): Flow<List<Budget>> {
        return budgetDao.getAllActive().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAll(): Flow<List<Budget>> {
        return budgetDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getById(id: Long): Flow<Budget?> {
        return budgetDao.getById(id).map { it?.toDomain() }
    }

    override suspend fun getByIdOnce(id: Long): Budget? {
        return budgetDao.getByIdOnce(id)?.toDomain()
    }

    override fun getByCategoryId(categoryId: Long): Flow<List<Budget>> {
        return budgetDao.getByCategoryId(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getByPeriod(period: BudgetPeriod): Flow<List<Budget>> {
        return budgetDao.getByPeriod(period.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insert(budget: Budget): Long {
        return budgetDao.insert(budget.toEntity())
    }

    override suspend fun update(budget: Budget) {
        budgetDao.update(budget.toEntity())
    }

    override suspend fun delete(budget: Budget) {
        budgetDao.delete(budget.toEntity())
    }

    override suspend fun deleteById(id: Long) {
        budgetDao.deleteById(id)
    }
}
