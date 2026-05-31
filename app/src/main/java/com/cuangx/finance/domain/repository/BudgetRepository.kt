package com.cuangx.finance.domain.repository

import com.cuangx.finance.domain.model.Budget
import com.cuangx.finance.domain.model.BudgetPeriod
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getAllActive(): Flow<List<Budget>>
    fun getAll(): Flow<List<Budget>>
    fun getById(id: Long): Flow<Budget?>
    suspend fun getByIdOnce(id: Long): Budget?
    fun getByCategoryId(categoryId: Long): Flow<List<Budget>>
    fun getByPeriod(period: BudgetPeriod): Flow<List<Budget>>
    suspend fun insert(budget: Budget): Long
    suspend fun update(budget: Budget)
    suspend fun delete(budget: Budget)
    suspend fun deleteById(id: Long)
}
