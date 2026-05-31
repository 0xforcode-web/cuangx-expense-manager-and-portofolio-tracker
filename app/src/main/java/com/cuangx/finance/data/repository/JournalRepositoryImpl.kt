package com.cuangx.finance.data.repository

import androidx.room.withTransaction
import com.cuangx.finance.core.database.CuangXDatabase
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
    private val transactionDao: TransactionDao,
    private val database: CuangXDatabase
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
        return database.withTransaction {
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

            // Update journal entry with transactionId link
            journalEntryDao.update(entry.copy(id = journalId, transactionId = transactionId).toEntity())

            journalId
        }
    }

    override suspend fun update(entry: JournalEntry) {
        database.withTransaction {
            // Reverse old entry's balance effect
            val oldEntryEntity = journalEntryDao.getByIdOnce(entry.id)
            if (oldEntryEntity != null) {
                val oldDomain = oldEntryEntity.toDomain()
                val oldAmount = when (oldDomain.action) {
                    JournalAction.BUY -> oldDomain.totalAmount
                    JournalAction.SELL -> (oldDomain.quantity * oldDomain.price) - oldDomain.fee
                    JournalAction.DIVIDEND -> oldDomain.quantity * oldDomain.price
                }
                val reverseChange = when (oldDomain.action) {
                    JournalAction.BUY -> oldAmount
                    JournalAction.SELL -> -oldAmount
                    JournalAction.DIVIDEND -> -oldAmount
                }
                accountDao.updateBalance(oldDomain.accountId, reverseChange)

                // Update associated transaction if exists
                if (oldEntryEntity.transactionId != null) {
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

                    val oldTransaction = transactionDao.getByIdOnce(oldEntryEntity.transactionId)
                    if (oldTransaction != null) {
                        transactionDao.update(
                            oldTransaction.copy(
                                type = transactionType.name,
                                amount = transactionAmount,
                                accountId = entry.accountId,
                                date = entry.date,
                                note = note
                            )
                        )
                    }
                }
            }

            // Update entry
            journalEntryDao.update(entry.toEntity())

            // Apply new balance effect
            val newAmount = when (entry.action) {
                JournalAction.BUY -> entry.totalAmount
                JournalAction.SELL -> (entry.quantity * entry.price) - entry.fee
                JournalAction.DIVIDEND -> entry.quantity * entry.price
            }
            val newChange = when (entry.action) {
                JournalAction.BUY -> -newAmount
                JournalAction.SELL -> newAmount
                JournalAction.DIVIDEND -> newAmount
            }
            accountDao.updateBalance(entry.accountId, newChange)
        }
    }

    override suspend fun delete(entry: JournalEntry) {
        database.withTransaction {
            // Reverse balance effect
            val amount = when (entry.action) {
                JournalAction.BUY -> entry.totalAmount
                JournalAction.SELL -> (entry.quantity * entry.price) - entry.fee
                JournalAction.DIVIDEND -> entry.quantity * entry.price
            }
            val reverseChange = when (entry.action) {
                JournalAction.BUY -> amount
                JournalAction.SELL -> -amount
                JournalAction.DIVIDEND -> -amount
            }
            accountDao.updateBalance(entry.accountId, reverseChange)

            // Delete associated transaction
            if (entry.transactionId != null) {
                transactionDao.deleteById(entry.transactionId)
            }

            // Delete journal entry
            journalEntryDao.delete(entry.toEntity())
        }
    }

    override suspend fun deleteById(id: Long) {
        val entry = journalEntryDao.getByIdOnce(id)?.toDomain() ?: return
        delete(entry)
    }

    override suspend fun getCount(): Int {
        return journalEntryDao.getCount()
    }
}
