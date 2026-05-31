package com.cuangx.finance.data.repository

import com.cuangx.finance.core.database.dao.AccountDao
import com.cuangx.finance.core.database.dao.TransactionDao
import com.cuangx.finance.core.database.mapper.toDomain
import com.cuangx.finance.core.database.mapper.toEntity
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionType
import com.cuangx.finance.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao
) : TransactionRepository {

    override fun getAll(): Flow<List<Transaction>> {
        return transactionDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> {
        return transactionDao.getByDateRange(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getByAccountId(accountId: Long): Flow<List<Transaction>> {
        return transactionDao.getByAccountId(accountId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getByCategoryId(categoryId: Long): Flow<List<Transaction>> {
        return transactionDao.getByCategoryId(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getByType(type: TransactionType): Flow<List<Transaction>> {
        return transactionDao.getByType(type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getById(id: Long): Flow<Transaction?> {
        return transactionDao.getById(id).map { it?.toDomain() }
    }

    override suspend fun getByIdOnce(id: Long): Transaction? {
        return transactionDao.getByIdOnce(id)?.toDomain()
    }

    override suspend fun getExpenseByCategoryAndDateRange(
        categoryId: Long,
        startDate: Long,
        endDate: Long
    ): Double {
        return transactionDao.getExpenseByCategoryAndDateRange(categoryId, startDate, endDate)
    }

    override fun getTotalIncomeByDateRange(startDate: Long, endDate: Long): Flow<Double> {
        return transactionDao.getTotalIncomeByDateRange(startDate, endDate)
    }

    override fun getTotalExpenseByDateRange(startDate: Long, endDate: Long): Flow<Double> {
        return transactionDao.getTotalExpenseByDateRange(startDate, endDate)
    }

    override fun getBookmarked(): Flow<List<Transaction>> {
        return transactionDao.getBookmarked().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecent(limit: Int): Flow<List<Transaction>> {
        return transactionDao.getRecent(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insert(transaction: Transaction): Long {
        val id = transactionDao.insert(transaction.toEntity())
        updateAccountBalance(transaction, isNew = true)
        return id
    }

    override suspend fun update(transaction: Transaction) {
        val oldTransaction = transactionDao.getByIdOnce(transaction.id)
        if (oldTransaction != null) {
            reverseAccountBalance(oldTransaction.toDomain())
        }
        transactionDao.update(transaction.toEntity())
        updateAccountBalance(transaction, isNew = true)
    }

    override suspend fun delete(transaction: Transaction) {
        reverseAccountBalance(transaction)
        transactionDao.delete(transaction.toEntity())
    }

    override suspend fun deleteById(id: Long) {
        val transaction = transactionDao.getByIdOnce(id)
        if (transaction != null) {
            reverseAccountBalance(transaction.toDomain())
            transactionDao.deleteById(id)
        }
    }

    override suspend fun getCount(): Int {
        return transactionDao.getCount()
    }

    private suspend fun updateAccountBalance(transaction: Transaction, isNew: Boolean) {
        when (transaction.type) {
            TransactionType.INCOME -> {
                accountDao.updateBalance(transaction.accountId, transaction.amount)
            }
            TransactionType.EXPENSE -> {
                accountDao.updateBalance(transaction.accountId, -transaction.amount)
            }
            TransactionType.TRANSFER -> {
                accountDao.updateBalance(transaction.accountId, -transaction.amount)
                transaction.toAccountId?.let {
                    accountDao.updateBalance(it, transaction.amount)
                }
            }
        }
    }

    private suspend fun reverseAccountBalance(transaction: Transaction) {
        when (transaction.type) {
            TransactionType.INCOME -> {
                accountDao.updateBalance(transaction.accountId, -transaction.amount)
            }
            TransactionType.EXPENSE -> {
                accountDao.updateBalance(transaction.accountId, transaction.amount)
            }
            TransactionType.TRANSFER -> {
                accountDao.updateBalance(transaction.accountId, transaction.amount)
                transaction.toAccountId?.let {
                    accountDao.updateBalance(it, -transaction.amount)
                }
            }
        }
    }
}
