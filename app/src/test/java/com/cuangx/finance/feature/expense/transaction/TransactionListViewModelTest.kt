package com.cuangx.finance.feature.expense.transaction

import com.cuangx.finance.core.util.DateUtils
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionType
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
class TransactionListViewModelTest {

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
    fun updateSelectedMode_switchesRepositoryRangeBetweenMonthAndYear() = runTest(testDispatcher) {
        val repository = RecordingTransactionRepository()
        val viewModel = TransactionListViewModel(repository)

        backgroundScope.launch { viewModel.uiState.collect() }
        advanceUntilIdle()

        val initialAnchor = viewModel.uiState.value.anchorDate
        assertEquals(
            DateUtils.getStartOfMonth(initialAnchor) to DateUtils.getEndOfMonth(initialAnchor),
            repository.lastRange,
        )

        viewModel.updateSelectedMode(ExpenseViewMode.MONTHLY)
        advanceUntilIdle()

        assertEquals(
            DateUtils.getStartOfYear(initialAnchor) to DateUtils.getEndOfYear(initialAnchor),
            repository.lastRange,
        )
    }

    @Test
    fun goToPreviousPeriod_shiftsByMonthForDailyAndByYearForTotal() = runTest(testDispatcher) {
        val repository = RecordingTransactionRepository()
        val viewModel = TransactionListViewModel(repository)

        backgroundScope.launch { viewModel.uiState.collect() }
        advanceUntilIdle()

        val initialAnchor = viewModel.uiState.value.anchorDate
        viewModel.goToPreviousPeriod()
        advanceUntilIdle()

        assertEquals(add(initialAnchor, Calendar.MONTH, -1), viewModel.uiState.value.anchorDate)

        viewModel.updateSelectedMode(ExpenseViewMode.TOTAL)
        advanceUntilIdle()
        val totalAnchor = viewModel.uiState.value.anchorDate

        viewModel.goToPreviousPeriod()
        advanceUntilIdle()

        assertEquals(add(totalAnchor, Calendar.YEAR, -1), viewModel.uiState.value.anchorDate)
    }

    private fun add(timestamp: Long, field: Int, amount: Int): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            add(field, amount)
        }.timeInMillis
    }

    private class RecordingTransactionRepository : TransactionRepository {
        private val transactions = MutableStateFlow<List<Transaction>>(emptyList())
        private val total = MutableStateFlow(0.0)
        var lastRange: Pair<Long, Long>? = null

        override fun getAll(): Flow<List<Transaction>> = transactions

        override fun getByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> {
            lastRange = startDate to endDate
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
}
