package com.cuangx.finance.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cuangx.finance.core.database.dao.AccountDao
import com.cuangx.finance.core.database.dao.BudgetDao
import com.cuangx.finance.core.database.dao.CategoryDao
import com.cuangx.finance.core.database.dao.DebtReceivableDao
import com.cuangx.finance.core.database.dao.DebtReceivablePaymentDao
import com.cuangx.finance.core.database.dao.DividendRecordDao
import com.cuangx.finance.core.database.dao.HoldingDao
import com.cuangx.finance.core.database.dao.JournalEntryDao
import com.cuangx.finance.core.database.dao.PriceCacheDao
import com.cuangx.finance.core.database.dao.RecurringTransactionDao
import com.cuangx.finance.core.database.dao.TransactionDao
import com.cuangx.finance.core.database.entity.AccountEntity
import com.cuangx.finance.core.database.entity.BudgetEntity
import com.cuangx.finance.core.database.entity.CategoryEntity
import com.cuangx.finance.core.database.entity.DebtReceivableEntity
import com.cuangx.finance.core.database.entity.DebtReceivablePaymentEntity
import com.cuangx.finance.core.database.entity.DividendRecordEntity
import com.cuangx.finance.core.database.entity.HoldingEntity
import com.cuangx.finance.core.database.entity.JournalEntryEntity
import com.cuangx.finance.core.database.entity.PriceCacheEntity
import com.cuangx.finance.core.database.entity.RecurringTransactionEntity
import com.cuangx.finance.core.database.entity.TransactionEntity

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        RecurringTransactionEntity::class,
        HoldingEntity::class,
        JournalEntryEntity::class,
        DividendRecordEntity::class,
        PriceCacheEntity::class,
        DebtReceivableEntity::class,
        DebtReceivablePaymentEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CuangXDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun recurringTransactionDao(): RecurringTransactionDao
    abstract fun holdingDao(): HoldingDao
    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun dividendRecordDao(): DividendRecordDao
    abstract fun priceCacheDao(): PriceCacheDao
    abstract fun debtReceivableDao(): DebtReceivableDao
    abstract fun debtReceivablePaymentDao(): DebtReceivablePaymentDao

    companion object {
        const val DATABASE_NAME = "cuangx_finance.db"
    }
}
