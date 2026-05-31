package com.cuangx.finance.feature.expense.transaction

import com.cuangx.finance.core.util.DateUtils
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionType
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionSummaryCalculatorTest {

    @Test
    fun dailyGroups_groupsByStartOfDayWithTotalsAndDescendingOrder() {
        val may10Morning = timestamp(2026, Calendar.MAY, 10, 8, 30)
        val may10Night = timestamp(2026, Calendar.MAY, 10, 21, 15)
        val may10Later = timestamp(2026, Calendar.MAY, 10, 22, 15)
        val may11 = timestamp(2026, Calendar.MAY, 11, 9, 0)
        val transfer = transaction(id = 4, type = TransactionType.TRANSFER, amount = 75.0, date = may10Later)

        val groups = TransactionSummaryCalculator.dailyGroups(
            listOf(
                transaction(id = 1, type = TransactionType.EXPENSE, amount = 125.0, date = may10Morning),
                transaction(id = 2, type = TransactionType.INCOME, amount = 500.0, date = may10Night),
                transaction(id = 3, type = TransactionType.EXPENSE, amount = 40.0, date = may11),
                transfer,
            )
        )

        assertEquals(listOf(DateUtils.getStartOfDay(may11), DateUtils.getStartOfDay(may10Morning)), groups.map { it.date })
        assertEquals(0.0, groups[0].income, 0.0)
        assertEquals(40.0, groups[0].expense, 0.0)
        assertEquals(-40.0, groups[0].balance, 0.0)
        assertEquals(500.0, groups[1].income, 0.0)
        assertEquals(125.0, groups[1].expense, 0.0)
        assertEquals(375.0, groups[1].balance, 0.0)
        assertEquals(listOf(4L, 2L, 1L), groups[1].transactions.map { it.id })
    }

    @Test
    fun calendarCells_returnsSixSundayWeeksWithPerDayTotalsForAnchorMonth() {
        val feb3 = timestamp(2024, Calendar.FEBRUARY, 3, 10, 0)
        val mar1 = timestamp(2024, Calendar.MARCH, 1, 12, 0)

        val cells = TransactionSummaryCalculator.calendarCells(
            anchorDate = timestamp(2024, Calendar.FEBRUARY, 15, 9, 0),
            transactions = listOf(
                transaction(id = 1, type = TransactionType.INCOME, amount = 300.0, date = feb3),
                transaction(id = 2, type = TransactionType.EXPENSE, amount = 80.0, date = feb3),
                transaction(id = 3, type = TransactionType.EXPENSE, amount = 25.0, date = mar1),
            )
        )

        assertEquals(42, cells.size)
        assertEquals(DateUtils.getStartOfDay(timestamp(2024, Calendar.JANUARY, 28)), cells.first().date)
        assertEquals(DateUtils.getStartOfDay(timestamp(2024, Calendar.MARCH, 9)), cells.last().date)
        assertFalse(cells.first().isInCurrentMonth)

        val feb3Cell = cells.first { it.date == DateUtils.getStartOfDay(feb3) }
        assertEquals(3, feb3Cell.dayOfMonth)
        assertTrue(feb3Cell.isInCurrentMonth)
        assertEquals(300.0, feb3Cell.income, 0.0)
        assertEquals(80.0, feb3Cell.expense, 0.0)
        assertEquals(220.0, feb3Cell.balance, 0.0)

        val mar1Cell = cells.first { it.date == DateUtils.getStartOfDay(mar1) }
        assertFalse(mar1Cell.isInCurrentMonth)
        assertEquals(0.0, mar1Cell.income, 0.0)
        assertEquals(25.0, mar1Cell.expense, 0.0)
        assertEquals(-25.0, mar1Cell.balance, 0.0)
    }

    @Test
    fun noteGroups_keepsOnlyTransactionsWithNonBlankNotes() {
        val may10 = timestamp(2026, Calendar.MAY, 10, 10, 0)
        val may11 = timestamp(2026, Calendar.MAY, 11, 10, 0)

        val groups = TransactionSummaryCalculator.noteGroups(
            listOf(
                transaction(id = 1, type = TransactionType.EXPENSE, amount = 20.0, date = may10, note = "coffee"),
                transaction(id = 2, type = TransactionType.INCOME, amount = 100.0, date = may10, note = "   "),
                transaction(id = 3, type = TransactionType.EXPENSE, amount = 35.0, date = may11, note = "lunch"),
                transaction(id = 4, type = TransactionType.INCOME, amount = 80.0, date = may11, note = ""),
            )
        )

        assertEquals(listOf(DateUtils.getStartOfDay(may11), DateUtils.getStartOfDay(may10)), groups.map { it.date })
        assertEquals(listOf(3L), groups[0].transactions.map { it.id })
        assertEquals(35.0, groups[0].expense, 0.0)
        assertEquals(listOf(1L), groups[1].transactions.map { it.id })
        assertEquals(20.0, groups[1].expense, 0.0)
    }

    @Test
    fun monthlySummaries_returnsAllMonthsDescendingWithYearTotalsOnly() {
        val summaries = TransactionSummaryCalculator.monthlySummaries(
            year = 2026,
            transactions = listOf(
                transaction(id = 1, type = TransactionType.INCOME, amount = 1000.0, date = timestamp(2026, Calendar.JANUARY, 5)),
                transaction(id = 2, type = TransactionType.EXPENSE, amount = 250.0, date = timestamp(2026, Calendar.JANUARY, 8)),
                transaction(id = 3, type = TransactionType.EXPENSE, amount = 90.0, date = timestamp(2026, Calendar.DECEMBER, 2)),
                transaction(id = 4, type = TransactionType.INCOME, amount = 777.0, date = timestamp(2025, Calendar.DECEMBER, 2)),
            )
        )

        assertEquals(12, summaries.size)
        assertEquals(Calendar.DECEMBER, summaries.first().month)
        assertEquals(Calendar.JANUARY, summaries.last().month)

        val december = summaries.first()
        assertEquals(2026, december.year)
        assertEquals(0.0, december.income, 0.0)
        assertEquals(90.0, december.expense, 0.0)
        assertEquals(-90.0, december.balance, 0.0)

        val january = summaries.last()
        assertEquals(1000.0, january.income, 0.0)
        assertEquals(250.0, january.expense, 0.0)
        assertEquals(750.0, january.balance, 0.0)

        val november = summaries[1]
        assertEquals(Calendar.NOVEMBER, november.month)
        assertEquals(0.0, november.income, 0.0)
        assertEquals(0.0, november.expense, 0.0)
        assertEquals(0.0, november.balance, 0.0)
    }

    private fun transaction(
        id: Long,
        type: TransactionType,
        amount: Double,
        date: Long,
        note: String = "",
    ): Transaction = Transaction(
        id = id,
        type = type,
        amount = amount,
        accountId = 1,
        date = date,
        note = note,
    )

    private fun timestamp(
        year: Int,
        month: Int,
        day: Int,
        hour: Int = 0,
        minute: Int = 0,
    ): Long = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
