package com.cuangx.finance.data.repository

import com.cuangx.finance.core.database.dao.AccountDao
import com.cuangx.finance.core.database.mapper.toDomain
import com.cuangx.finance.core.database.mapper.toEntity
import com.cuangx.finance.domain.model.Account
import com.cuangx.finance.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {

    override fun getAllActive(): Flow<List<Account>> {
        return accountDao.getAllActive().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAll(): Flow<List<Account>> {
        return accountDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getById(id: Long): Flow<Account?> {
        return accountDao.getById(id).map { it?.toDomain() }
    }

    override suspend fun getByIdOnce(id: Long): Account? {
        return accountDao.getByIdOnce(id)?.toDomain()
    }

    override fun getTotalBalance(): Flow<Double> {
        return accountDao.getTotalBalance().map { it ?: 0.0 }
    }

    override suspend fun updateBalance(id: Long, amount: Double) {
        accountDao.updateBalance(id, amount)
    }

    override suspend fun getBalance(id: Long): Double? {
        return accountDao.getBalance(id)
    }

    override suspend fun insert(account: Account): Long {
        return accountDao.insert(account.toEntity())
    }

    override suspend fun update(account: Account) {
        accountDao.update(account.toEntity())
    }

    override suspend fun delete(account: Account) {
        accountDao.delete(account.toEntity())
    }

    override suspend fun archive(id: Long) {
        accountDao.archive(id)
    }

    override suspend fun getCount(): Int {
        return accountDao.getCount()
    }
}
