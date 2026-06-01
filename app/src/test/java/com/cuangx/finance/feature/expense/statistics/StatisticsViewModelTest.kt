package com.cuangx.finance.feature.expense.statistics

import com.cuangx.finance.core.util.DateUtils
import com.cuangx.finance.domain.model.Category
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionType
import com.cuangx.finance.domain.repository.CategoryRepository
import com.cuangx.finance.domain.repository.TransactionRepository
import java.util.Calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun goToPreviousMonth_updatesStatisticsRepositoryRange() = runTest(testDispatcher) {
        val transactionRepository = RecordingTransactionRepository()
        val viewModel = StatisticsViewModel(
            transactionRepository = transactionRepository,
            categoryRepository = EmptyCategoryRepository()
        )
        val collectJob = backgroundScope.launch { viewModel.uiState.collect() }

        advanceUntilIdle()

        val initialAnchor = viewModel.uiState.value.anchorDate
        assertEquals(
            DateUtils.getStartOfMonth(initialAnchor) to DateUtils.getEndOfMonth(initialAnchor),
            transactionRepository.lastTransactionRange,
        )

        viewModel.goToPreviousMonth()
        advanceUntilIdle()

        val previousAnchor = Calendar.getInstance().apply {
            timeInMillis = initialAnchor
            add(Calendar.MONTH, -1)
        }.timeInMillis
        assertEquals(
            DateUtils.getStartOfMonth(previousAnchor) to DateUtils.getEndOfMonth(previousAnchor),
            transactionRepository.lastTransactionRange,
        )

        collectJob.cancel()
    }

    private class RecordingTransactionRepository : TransactionRepository {
        private val transactions = MutableStateFlow<List<Transaction>>(emptyList())
        private val total = MutableStateFlow(0.0)
        var lastTransactionRange: Pair<Long, Long>? = null

        override fun getAll(): Flow<List<Transaction>> = transactions

        override fun getByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> {
            lastTransactionRange = startDate to endDate
            return transactions
        }

        override fun getByAccountId(accountId: Long): Flow<List<Transaction>> = transactions
        override fun getByCategoryId(categoryId: Long): Flow<List<Transaction>> = transactions
        override fun getByType(type: TransactionType): Flow<List<Transaction>> = transactions
        override fun getById(id: Long): Flow<Transaction?> = MutableStateFlow(null)
        override suspend fun getByIdOnce(id: Long): Transaction? = null
        override suspend fun getExpenseByCategoryAndDateRange(categoryId: Long, startDate: Long, endDate: Long): Double = 0.0
        override fun getTotalIncomeByDateRange(startDate: Long, endDate: Long): Flow<Double> = total
        override fun getTotalExpenseByDateRange(startDate: Long, endDate: Long): Flow<Double> = total
        override fun getBookmarked(): Flow<List<Transaction>> = transactions
        override fun getRecent(limit: Int): Flow<List<Transaction>> = transactions
        override suspend fun insert(transaction: Transaction): Long = transaction.id
        override suspend fun update(transaction: Transaction) = Unit
        override suspend fun delete(transaction: Transaction) = Unit
        override suspend fun deleteById(id: Long) = Unit
        override suspend fun getCount(): Int = 0
    }

    private class EmptyCategoryRepository : CategoryRepository {
        private val categories = MutableStateFlow<List<Category>>(emptyList())

        override fun getAll(): Flow<List<Category>> = categories
        override fun getByType(type: TransactionType): Flow<List<Category>> = categories
        override fun getAllParents(): Flow<List<Category>> = categories
        override fun getSubCategories(parentId: Long): Flow<List<Category>> = categories
        override fun getById(id: Long): Flow<Category?> = MutableStateFlow(null)
        override suspend fun getByIdOnce(id: Long): Category? = null
        override suspend fun getByNameAndType(name: String, type: TransactionType): Category? = null
        override suspend fun insert(category: Category): Long = category.id
        override suspend fun update(category: Category) = Unit
        override suspend fun delete(category: Category) = Unit
        override suspend fun getCount(): Int = 0
    }
}
