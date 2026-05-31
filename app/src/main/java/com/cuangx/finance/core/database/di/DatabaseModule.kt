package com.cuangx.finance.core.database.di

import android.content.Context
import androidx.room.Room
import com.cuangx.finance.core.database.CuangXDatabase
import com.cuangx.finance.core.database.DatabaseCallback
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CuangXDatabase {
        return Room.databaseBuilder(
            context,
            CuangXDatabase::class.java,
            CuangXDatabase.DATABASE_NAME
        ).addCallback(DatabaseCallback())
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAccountDao(db: CuangXDatabase): AccountDao = db.accountDao()

    @Provides
    fun provideCategoryDao(db: CuangXDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideTransactionDao(db: CuangXDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideBudgetDao(db: CuangXDatabase): BudgetDao = db.budgetDao()

    @Provides
    fun provideRecurringTransactionDao(db: CuangXDatabase): RecurringTransactionDao = db.recurringTransactionDao()

    @Provides
    fun provideHoldingDao(db: CuangXDatabase): HoldingDao = db.holdingDao()

    @Provides
    fun provideJournalEntryDao(db: CuangXDatabase): JournalEntryDao = db.journalEntryDao()

    @Provides
    fun provideDividendRecordDao(db: CuangXDatabase): DividendRecordDao = db.dividendRecordDao()

    @Provides
    fun providePriceCacheDao(db: CuangXDatabase): PriceCacheDao = db.priceCacheDao()

    @Provides
    fun provideDebtReceivableDao(db: CuangXDatabase): DebtReceivableDao = db.debtReceivableDao()

    @Provides
    fun provideDebtReceivablePaymentDao(db: CuangXDatabase): DebtReceivablePaymentDao = db.debtReceivablePaymentDao()
}
