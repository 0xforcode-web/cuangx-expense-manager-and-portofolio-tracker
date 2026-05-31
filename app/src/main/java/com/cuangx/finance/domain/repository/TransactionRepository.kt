package com.cuangx.finance.domain.repository

import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAll(): Flow<List<Transaction>>
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>>
    fun getByAccountId(accountId: Long): Flow<List<Transaction>>
    fun getByCategoryId(categoryId: Long): Flow<List<Transaction>>
    fun getByType(type: TransactionType): Flow<List<Transaction>>
    fun getById(id: Long): Flow<Transaction?>
    suspend fun getByIdOnce(id: Long): Transaction?
    suspend fun getExpenseByCategoryAndDateRange(categoryId: Long, startDate: Long, endDate: Long): Double
    fun getTotalIncomeByDateRange(startDate: Long, endDate: Long): Flow<Double>
    fun getTotalExpenseByDateRange(startDate: Long, endDate: Long): Flow<Double>
    fun getBookmarked(): Flow<List<Transaction>>
    fun getRecent(limit: Int): Flow<List<Transaction>>
    suspend fun insert(transaction: Transaction): Long
    suspend fun update(transaction: Transaction)
    suspend fun delete(transaction: Transaction)
    suspend fun deleteById(id: Long)
    suspend fun getCount(): Int
}
