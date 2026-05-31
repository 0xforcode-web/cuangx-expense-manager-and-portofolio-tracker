package com.cuangx.finance.feature.expense.transaction

import com.cuangx.finance.core.util.DateUtils
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionType
import java.util.Calendar

object TransactionSummaryCalculator {

    fun dailyGroups(transactions: List<Transaction>): List<TransactionDayGroup> {
        return transactions
            .groupBy { DateUtils.getStartOfDay(it.date) }
            .map { (date, dayTransactions) ->
                val sortedTransactions = dayTransactions.sortedByDescending { it.date }
                val totals = totalsFor(dayTransactions)
                TransactionDayGroup(
                    date = date,
                    income = totals.income,
                    expense = totals.expense,
                    balance = totals.balance,
                    transactions = sortedTransactions,
                )
            }
            .sortedByDescending { it.date }
    }

    fun noteGroups(transactions: List<Transaction>): List<TransactionDayGroup> {
        return dailyGroups(transactions.filter { it.note.isNotBlank() })
    }

    fun calendarCells(anchorDate: Long, transactions: List<Transaction>): List<TransactionCalendarCell> {
        val activeMonth = Calendar.getInstance().apply {
            timeInMillis = anchorDate
        }
        val activeYear = activeMonth.get(Calendar.YEAR)
        val activeMonthIndex = activeMonth.get(Calendar.MONTH)

        val firstVisibleDay = Calendar.getInstance().apply {
            timeInMillis = anchorDate
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, -(get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY))
        }
        val transactionsByDay = transactions.groupBy { DateUtils.getStartOfDay(it.date) }

        return (0 until CALENDAR_CELL_COUNT).map { offset ->
            val cellDate = firstVisibleDay.clone() as Calendar
            cellDate.add(Calendar.DAY_OF_MONTH, offset)

            val date = cellDate.timeInMillis
            val dayTransactions = transactionsByDay[date].orEmpty()
            val totals = totalsFor(dayTransactions)
            TransactionCalendarCell(
                date = date,
                dayOfMonth = cellDate.get(Calendar.DAY_OF_MONTH),
                isInCurrentMonth = cellDate.get(Calendar.YEAR) == activeYear &&
                    cellDate.get(Calendar.MONTH) == activeMonthIndex,
                income = totals.income,
                expense = totals.expense,
                balance = totals.balance,
            )
        }
    }

    fun monthlySummaries(year: Int, transactions: List<Transaction>): List<TransactionMonthSummary> {
        val transactionsByMonth = transactions
            .filter { transactionYear(it) == year }
            .groupBy { transactionMonth(it) }

        return (Calendar.DECEMBER downTo Calendar.JANUARY).map { month ->
            val totals = totalsFor(transactionsByMonth[month].orEmpty())
            TransactionMonthSummary(
                year = year,
                month = month,
                income = totals.income,
                expense = totals.expense,
                balance = totals.balance,
            )
        }
    }

    fun totalSummary(transactions: List<Transaction>): TransactionTotalSummary {
        val totals = totalsFor(transactions)
        return TransactionTotalSummary(
            income = totals.income,
            expense = totals.expense,
            balance = totals.balance,
            transactionCount = transactions.size,
        )
    }

    private fun totalsFor(transactions: List<Transaction>): Totals {
        val income = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        val expense = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        return Totals(income = income, expense = expense, balance = income - expense)
    }

    private fun transactionYear(transaction: Transaction): Int {
        return Calendar.getInstance().apply {
            timeInMillis = transaction.date
        }.get(Calendar.YEAR)
    }

    private fun transactionMonth(transaction: Transaction): Int {
        return Calendar.getInstance().apply {
            timeInMillis = transaction.date
        }.get(Calendar.MONTH)
    }

    private data class Totals(
        val income: Double,
        val expense: Double,
        val balance: Double,
    )

    private const val CALENDAR_CELL_COUNT = 42
}
