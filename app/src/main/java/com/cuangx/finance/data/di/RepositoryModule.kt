package com.cuangx.finance.data.di

import com.cuangx.finance.data.repository.AccountRepositoryImpl
import com.cuangx.finance.data.repository.BudgetRepositoryImpl
import com.cuangx.finance.data.repository.CategoryRepositoryImpl
import com.cuangx.finance.data.repository.DashboardRepositoryImpl
import com.cuangx.finance.data.repository.DebtReceivablePaymentRepositoryImpl
import com.cuangx.finance.data.repository.DebtReceivableRepositoryImpl
import com.cuangx.finance.data.repository.DividendRepositoryImpl
import com.cuangx.finance.data.repository.HoldingRepositoryImpl
import com.cuangx.finance.data.repository.JournalRepositoryImpl
import com.cuangx.finance.data.repository.PriceRepositoryImpl
import com.cuangx.finance.data.repository.RecurringRepositoryImpl
import com.cuangx.finance.data.repository.TransactionRepositoryImpl
import com.cuangx.finance.domain.repository.AccountRepository
import com.cuangx.finance.domain.repository.BudgetRepository
import com.cuangx.finance.domain.repository.CategoryRepository
import com.cuangx.finance.domain.repository.DashboardRepository
import com.cuangx.finance.domain.repository.DebtReceivablePaymentRepository
import com.cuangx.finance.domain.repository.DebtReceivableRepository
import com.cuangx.finance.domain.repository.DividendRepository
import com.cuangx.finance.domain.repository.HoldingRepository
import com.cuangx.finance.domain.repository.JournalRepository
import com.cuangx.finance.domain.repository.PriceRepository
import com.cuangx.finance.domain.repository.RecurringRepository
import com.cuangx.finance.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(impl: BudgetRepositoryImpl): BudgetRepository

    @Binds
    @Singleton
    abstract fun bindRecurringRepository(impl: RecurringRepositoryImpl): RecurringRepository

    @Binds
    @Singleton
    abstract fun bindHoldingRepository(impl: HoldingRepositoryImpl): HoldingRepository

    @Binds
    @Singleton
    abstract fun bindJournalRepository(impl: JournalRepositoryImpl): JournalRepository

    @Binds
    @Singleton
    abstract fun bindPriceRepository(impl: PriceRepositoryImpl): PriceRepository

    @Binds
    @Singleton
    abstract fun bindDividendRepository(impl: DividendRepositoryImpl): DividendRepository

    @Binds
    @Singleton
    abstract fun bindDebtReceivableRepository(impl: DebtReceivableRepositoryImpl): DebtReceivableRepository

    @Binds
    @Singleton
    abstract fun bindDebtReceivablePaymentRepository(impl: DebtReceivablePaymentRepositoryImpl): DebtReceivablePaymentRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository
}
