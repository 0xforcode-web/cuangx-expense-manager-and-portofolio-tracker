package com.cuangx.finance.feature.expense.transaction

import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionSource
import com.cuangx.finance.domain.model.TransactionType
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionSummaryCalculatorTest {

    @Test
    fun `daily groups transactions by date and sums income expense balance`() {
        val may1Income = transaction(id = 1, amount = 500_000.0, type = TransactionType.INCOME, date = day(2026, Calendar.MAY, 1), note = "salary")
        val may1Expense = transaction(id = 2, amount = 125_000.0, type = TransactionType.EXPENSE, date = day(2026, Calendar.MAY, 1), note = "food")
        val may2Expense = transaction(id = 3, amount = 75_000.0, type = TransactionType.EXPENSE, date = day(2026, Calendar.MAY, 2), note = "coffee")

        val groups = TransactionSummaryCalculator.dailyGroups(listOf(may1Income, may1Expense, may2Expense))

        assertEquals(2, groups.size)
        assertEquals(75_000.0, groups[0].expense, 0.0)
        assertEquals(2, groups[1].transactions.size)
        assertEquals(500_000.0, groups[1].income, 0.0)
        assertEquals(125_000.0, groups[1].expense, 0.0)
        assertEquals(375_000.0, groups[1].balance, 0.0)
    }

    @Test
    fun `calendar cells include every visible day and summarize matching transactions`() {
        val may1Income = transaction(id = 1, amount = 100_000.0, type = TransactionType.INCOME, date = day(2026, Calendar.MAY, 1))
        val may1Expense = transaction(id = 2, amount = 40_000.0, type = TransactionType.EXPENSE, date = day(2026, Calendar.MAY, 1))

        val cells = TransactionSummaryCalculator.calendarCells(day(2026, Calendar.MAY, 15), listOf(may1Income, may1Expense))

        assertEquals(42, cells.size)
        val mayFirst = cells.first { it.dayOfMonth == 1 && it.isInCurrentMonth }
        assertEquals(100_000.0, mayFirst.income, 0.0)
        assertEquals(40_000.0, mayFirst.expense, 0.0)
        assertEquals(60_000.0, mayFirst.balance, 0.0)
    }

    @Test
    fun `note groups keep only transactions with non blank notes`() {
        val withNote = transaction(id = 1, amount = 50_000.0, type = TransactionType.EXPENSE, date = day(2026, Calendar.MAY, 1), note = "Ngopi")
        val blankNote = transaction(id = 2, amount = 60_000.0, type = TransactionType.EXPENSE, date = day(2026, Calendar.MAY, 1), note = " ")

        val groups = TransactionSummaryCalculator.noteGroups(listOf(withNote, blankNote))

        assertEquals(1, groups.size)
        assertEquals(listOf(withNote), groups.first().transactions)
    }

    @Test
    fun `monthly summaries include months in descending order for active year`() {
        val januaryIncome = transaction(id = 1, amount = 200_000.0, type = TransactionType.INCOME, date = day(2026, Calendar.JANUARY, 10))
        val mayExpense = transaction(id = 2, amount = 50_000.0, type = TransactionType.EXPENSE, date = day(2026, Calendar.MAY, 20))

        val summaries = TransactionSummaryCalculator.monthlySummaries(2026, listOf(januaryIncome, mayExpense))

        assertEquals(12, summaries.size)
        assertEquals(Calendar.DECEMBER, summaries.first().month)
        assertEquals(Calendar.JANUARY, summaries.last().month)
        assertTrue(summaries.any { it.month == Calendar.JANUARY && it.income == 200_000.0 })
        assertTrue(summaries.any { it.month == Calendar.MAY && it.expense == 50_000.0 })
    }

    private fun transaction(
        id: Long,
        amount: Double,
        type: TransactionType,
        date: Long,
        note: String = ""
    ) = Transaction(
        id = id,
        type = type,
        amount = amount,
        accountId = 1,
        categoryId = 1,
        date = date,
        note = note,
        source = TransactionSource.EXPENSE
    )

    private fun day(year: Int, month: Int, day: Int): Long {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
