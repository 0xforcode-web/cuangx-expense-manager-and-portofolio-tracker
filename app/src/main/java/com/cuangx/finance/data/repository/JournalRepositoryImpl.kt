package com.cuangx.finance.data.repository

import com.cuangx.finance.core.database.dao.AccountDao
import com.cuangx.finance.core.database.dao.JournalEntryDao
import com.cuangx.finance.core.database.dao.TransactionDao
import com.cuangx.finance.core.database.mapper.toDomain
import com.cuangx.finance.core.database.mapper.toEntity
import com.cuangx.finance.core.database.entity.TransactionEntity
import com.cuangx.finance.domain.model.JournalAction
import com.cuangx.finance.domain.model.JournalEntry
import com.cuangx.finance.domain.model.TransactionType
import com.cuangx.finance.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalRepositoryImpl @Inject constructor(
    private val journalEntryDao: JournalEntryDao,
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao
) : JournalRepository {

    override fun getAll(): Flow<List<JournalEntry>> {
        return journalEntryDao.getAll().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getByTicker(ticker: String): Flow<List<JournalEntry>> {
        return journalEntryDao.getByTicker(ticker).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getByAction(action: String): Flow<List<JournalEntry>> {
        return journalEntryDao.getByAction(action).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getByAccountId(accountId: Long): Flow<List<JournalEntry>> {
        return journalEntryDao.getByAccountId(accountId).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getByDateRange(startDate: Long, endDate: Long): Flow<List<JournalEntry>> {
        return journalEntryDao.getByDateRange(startDate, endDate).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getById(id: Long): Flow<JournalEntry?> {
        return journalEntryDao.getById(id).map { it?.toDomain() }
    }

    override suspend fun getByIdOnce(id: Long): JournalEntry? {
        return journalEntryDao.getByIdOnce(id)?.toDomain()
    }

    override suspend fun getByTickerOnce(ticker: String): List<JournalEntry> {
        return journalEntryDao.getByTickerOnce(ticker).map { it.toDomain() }
    }

    override suspend fun getAllTickers(): List<String> {
        return journalEntryDao.getAllTickers()
    }

    override suspend fun getCurrentQuantity(ticker: String): Double {
        return journalEntryDao.getCurrentQuantity(ticker)
    }

    override suspend fun insert(entry: JournalEntry): Long {
        val journalId = journalEntryDao.insert(entry.toEntity())

        val transactionType = when (entry.action) {
            JournalAction.BUY -> TransactionType.EXPENSE
            JournalAction.SELL -> TransactionType.INCOME
            JournalAction.DIVIDEND -> TransactionType.INCOME
        }

        val transactionAmount = when (entry.action) {
            JournalAction.BUY -> entry.totalAmount
            JournalAction.SELL -> (entry.quantity * entry.price) - entry.fee
            JournalAction.DIVIDEND -> entry.quantity * entry.price
        }

        val note = when (entry.action) {
            JournalAction.BUY -> "Beli ${entry.name} ${entry.quantity} @ ${entry.price}"
            JournalAction.SELL -> "Jual ${entry.name} ${entry.quantity} @ ${entry.price}"
            JournalAction.DIVIDEND -> "Dividen ${entry.name}"
        }

        val transactionId = transactionDao.insert(
            TransactionEntity(
                type = transactionType.name,
                amount = transactionAmount,
                accountId = entry.accountId,
                date = entry.date,
                note = note,
                linkedHoldingId = null,
                linkedDividendId = if (entry.action == JournalAction.DIVIDEND) journalId else null,
                source = "PORTFOLIO"
            )
        )

        val balanceChange = when (entry.action) {
            JournalAction.BUY -> -transactionAmount
            JournalAction.SELL -> transactionAmount
            JournalAction.DIVIDEND -> transactionAmount
        }
        accountDao.updateBalance(entry.accountId, balanceChange)

        journalEntryDao.insert(entry.copy(id = journalId, transactionId = transactionId).toEntity())

        return journalId
    }

    override suspend fun update(entry: JournalEntry) {
        journalEntryDao.update(entry.toEntity())
    }

    override suspend fun delete(entry: JournalEntry) {
        journalEntryDao.delete(entry.toEntity())
    }

    override suspend fun deleteById(id: Long) {
        journalEntryDao.deleteById(id)
    }

    override suspend fun getCount(): Int {
        return journalEntryDao.getCount()
    }
}
