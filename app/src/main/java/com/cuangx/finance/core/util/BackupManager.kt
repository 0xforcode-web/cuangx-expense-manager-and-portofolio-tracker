package com.cuangx.finance.core.util

import android.content.Context
import android.net.Uri
import com.cuangx.finance.core.database.CuangXDatabase
import com.cuangx.finance.core.database.entity.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: CuangXDatabase
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    data class DatabaseBackup(
        val accounts: List<AccountEntity>,
        val categories: List<CategoryEntity>,
        val transactions: List<TransactionEntity>,
        val budgets: List<BudgetEntity>,
        val recurringTransactions: List<RecurringTransactionEntity>,
        val holdings: List<HoldingEntity>,
        val journalEntries: List<JournalEntryEntity>,
        val dividendRecords: List<DividendRecordEntity>,
        val priceCache: List<PriceCacheEntity>,
        val debtsReceivables: List<DebtReceivableEntity>,
        val debtReceivablePayments: List<DebtReceivablePaymentEntity>
    )

    suspend fun exportBackup(): File = withContext(Dispatchers.IO) {
        val backup = DatabaseBackup(
            accounts = database.accountDao().getAll().first(),
            categories = database.categoryDao().getAll().first(),
            transactions = database.transactionDao().getAll().first(),
            budgets = database.budgetDao().getAll().first(),
            recurringTransactions = database.recurringTransactionDao().getAll().first(),
            holdings = database.holdingDao().getAll().first(),
            journalEntries = database.journalEntryDao().getAll().first(),
            dividendRecords = database.dividendRecordDao().getAll().first(),
            priceCache = database.priceCacheDao().getAll().first(),
            debtsReceivables = database.debtReceivableDao().getAll().first(),
            debtReceivablePayments = database.debtReceivablePaymentDao().getAll().first()
        )

        val adapter = moshi.adapter(DatabaseBackup::class.java)
        val json = adapter.toJson(backup)

        val file = File(context.cacheDir, "cuangx_backup_${System.currentTimeMillis()}.json")
        file.writeText(json)
        file
    }

    suspend fun restoreBackup(uri: Uri) = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext
        val json = inputStream.bufferedReader().use { it.readText() }
        
        val adapter = moshi.adapter(DatabaseBackup::class.java)
        val backup = adapter.fromJson(json) ?: return@withContext

        database.runInTransaction {
            // Use runBlocking or similar if needed, but runInTransaction should work fine with suspend calls if database is configured correctly.
            // Actually, Room's runInTransaction is not suspend-friendly easily without extra care.
            // But we can just use the DAOs directly here.
        }
        
        // Clear all tables first to avoid conflicts if desired, or use REPLACE strategy.
        // For simplicity and safety of data, we'll use a sequence of deletes and inserts.
        
        with(database) {
            // We'll use clearAllTables() but it might be too aggressive if we want to keep some data.
            // However, a RESTORE usually means overwriting.
            clearAllTables()

            backup.accounts.forEach { accountDao().insert(it) }
            backup.categories.forEach { categoryDao().insert(it) }
            backup.transactions.forEach { transactionDao().insert(it) }
            backup.budgets.forEach { budgetDao().insert(it) }
            backup.recurringTransactions.forEach { recurringTransactionDao().insert(it) }
            backup.holdings.forEach { holdingDao().insert(it) }
            backup.journalEntries.forEach { journalEntryDao().insert(it) }
            backup.dividendRecords.forEach { dividendRecordDao().insert(it) }
            backup.priceCache.forEach { priceCacheDao().insert(it) }
            backup.debtsReceivables.forEach { debtReceivableDao().insert(it) }
            backup.debtReceivablePayments.forEach { debtReceivablePaymentDao().insert(it) }
        }
    }
}
