package com.cuangx.finance.domain.repository

import com.cuangx.finance.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAllActive(): Flow<List<Account>>
    fun getAll(): Flow<List<Account>>
    fun getById(id: Long): Flow<Account?>
    suspend fun getByIdOnce(id: Long): Account?
    fun getTotalBalance(): Flow<Double>
    suspend fun updateBalance(id: Long, amount: Double)
    suspend fun getBalance(id: Long): Double?
    suspend fun insert(account: Account): Long
    suspend fun update(account: Account)
    suspend fun delete(account: Account)
    suspend fun archive(id: Long)
    suspend fun getCount(): Int
}
