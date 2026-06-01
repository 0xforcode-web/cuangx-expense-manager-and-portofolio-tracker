package com.cuangx.finance.feature.expense.transaction

import com.cuangx.finance.core.util.DateUtils
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionType
import java.util.Calendar

object TransactionSummaryCalculator {

    fun dailyGroups(transactions: List<Transaction>): List<TransactionDayGroup> {
        return transactions
            .groupBy { DateUtils.getStartOfDay(it.date) }
            .toSortedMap(compareByDescending { it })
            .map { (date, dayTransactions) ->
                val income = dayTransactions.sumByType(TransactionType.INCOME)
                val expense = dayTransactions.sumByType(TransactionType.EXPENSE)
                TransactionDayGroup(
                    date = date,
                    income = income,
                    expense = expense,
                    balance = income - expense,
                    transactions = dayTransactions.sortedByDescending { it.date }
                )
            }
    }

    fun noteGroups(transactions: List<Transaction>): List<TransactionDayGroup> {
        return dailyGroups(transactions.filter { it.note.isNotBlank() })
    }

    fun calendarCells(anchorDate: Long, transactions: List<Transaction>): List<TransactionCalendarCell> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = DateUtils.getStartOfMonth(anchorDate)
            firstDayOfWeek = Calendar.SUNDAY
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        }
        val currentMonth = Calendar.getInstance().apply { timeInMillis = anchorDate }.get(Calendar.MONTH)
        val transactionsByDay = transactions.groupBy { DateUtils.getStartOfDay(it.date) }

        return List(42) {
            val date = DateUtils.getStartOfDay(calendar.timeInMillis)
            val dayTransactions = transactionsByDay[date].orEmpty()
            val income = dayTransactions.sumByType(TransactionType.INCOME)
            val expense = dayTransactions.sumByType(TransactionType.EXPENSE)
            val cell = TransactionCalendarCell(
                date = date,
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
                isInCurrentMonth = calendar.get(Calendar.MONTH) == currentMonth,
                income = income,
                expense = expense,
                balance = income - expense
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            cell
        }
    }

    fun monthlySummaries(year: Int, transactions: List<Transaction>): List<TransactionMonthSummary> {
        val byMonth = transactions
            .filter { Calendar.getInstance().apply { timeInMillis = it.date }.get(Calendar.YEAR) == year }
            .groupBy { Calendar.getInstance().apply { timeInMillis = it.date }.get(Calendar.MONTH) }

        return (Calendar.DECEMBER downTo Calendar.JANUARY).map { month ->
            val monthTransactions = byMonth[month].orEmpty()
            val income = monthTransactions.sumByType(TransactionType.INCOME)
            val expense = monthTransactions.sumByType(TransactionType.EXPENSE)
            TransactionMonthSummary(
                year = year,
                month = month,
                income = income,
                expense = expense,
                balance = income - expense
            )
        }
    }

    fun totalSummary(transactions: List<Transaction>): TransactionTotalSummary {
        val income = transactions.sumByType(TransactionType.INCOME)
        val expense = transactions.sumByType(TransactionType.EXPENSE)
        return TransactionTotalSummary(
            income = income,
            expense = expense,
            balance = income - expense,
            transactionCount = transactions.size
        )
    }

    private fun List<Transaction>.sumByType(type: TransactionType): Double {
        return filter { it.type == type }.sumOf { it.amount }
    }
}
