package com.cuangx.finance.feature.expense.statistics

import com.cuangx.finance.domain.model.Category
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionSource
import com.cuangx.finance.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryBreakdownCalculatorTest {

    @Test
    fun `calculates sorted expense category percentages`() {
        val food = category(1, "Makanan", TransactionType.EXPENSE)
        val transport = category(2, "Transport", TransactionType.EXPENSE)
        val transactions = listOf(
            transaction(1, 80_000.0, TransactionType.EXPENSE, categoryId = food.id),
            transaction(2, 20_000.0, TransactionType.EXPENSE, categoryId = transport.id),
            transaction(3, 500_000.0, TransactionType.INCOME, categoryId = 9)
        )

        val breakdown = CategoryBreakdownCalculator.calculate(
            transactions = transactions,
            categories = listOf(food, transport),
            type = TransactionType.EXPENSE
        )

        assertEquals("Makanan", breakdown[0].category.name)
        assertEquals(80_000.0, breakdown[0].amount, 0.0)
        assertEquals(80.0, breakdown[0].percentage, 0.0)
        assertEquals("Transport", breakdown[1].category.name)
        assertEquals(20.0, breakdown[1].percentage, 0.0)
    }

    @Test
    fun `calculates income category percentages separately`() {
        val salary = category(1, "Salary", TransactionType.INCOME)
        val bonus = category(2, "Bonus", TransactionType.INCOME)
        val transactions = listOf(
            transaction(1, 1_500_000.0, TransactionType.INCOME, categoryId = salary.id),
            transaction(2, 500_000.0, TransactionType.INCOME, categoryId = bonus.id),
            transaction(3, 200_000.0, TransactionType.EXPENSE, categoryId = 7)
        )

        val breakdown = CategoryBreakdownCalculator.calculate(
            transactions = transactions,
            categories = listOf(salary, bonus),
            type = TransactionType.INCOME
        )

        assertEquals(75.0, breakdown[0].percentage, 0.0)
        assertEquals(25.0, breakdown[1].percentage, 0.0)
    }

    @Test
    fun `uses uncategorized fallback for missing category`() {
        val transactions = listOf(
            transaction(1, 100_000.0, TransactionType.EXPENSE, categoryId = 999)
        )

        val breakdown = CategoryBreakdownCalculator.calculate(
            transactions = transactions,
            categories = emptyList(),
            type = TransactionType.EXPENSE
        )

        assertEquals("Uncategorized", breakdown.single().category.name)
        assertEquals(100.0, breakdown.single().percentage, 0.0)
    }

    private fun category(id: Long, name: String, type: TransactionType) = Category(
        id = id,
        name = name,
        type = type
    )

    private fun transaction(
        id: Long,
        amount: Double,
        type: TransactionType,
        categoryId: Long
    ) = Transaction(
        id = id,
        type = type,
        amount = amount,
        accountId = 1,
        categoryId = categoryId,
        date = 0,
        note = "",
        source = TransactionSource.EXPENSE
    )
}
