package com.cuangx.finance.feature.expense.statistics

import com.cuangx.finance.domain.model.Category
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryBreakdownCalculatorTest {

    @Test
    fun calculate_returnsExpenseBreakdownSortedByAmountWithPercentages() {
        val food = category(id = 1, name = "Food", type = TransactionType.EXPENSE)
        val transport = category(id = 2, name = "Transport", type = TransactionType.EXPENSE)
        val salary = category(id = 3, name = "Salary", type = TransactionType.INCOME)

        val breakdown = CategoryBreakdownCalculator.calculate(
            transactions = listOf(
                transaction(type = TransactionType.EXPENSE, amount = 100.0, categoryId = food.id),
                transaction(type = TransactionType.EXPENSE, amount = 250.0, categoryId = transport.id),
                transaction(type = TransactionType.EXPENSE, amount = 50.0, categoryId = transport.id),
                transaction(type = TransactionType.INCOME, amount = 1_000.0, categoryId = salary.id),
            ),
            categories = listOf(food, transport, salary),
            type = TransactionType.EXPENSE,
        )

        assertEquals(listOf("Transport", "Food"), breakdown.map { it.category.name })
        assertEquals(300.0, breakdown[0].amount, 0.0)
        assertEquals(75.0, breakdown[0].percentage, 0.0)
        assertEquals(100.0, breakdown[1].amount, 0.0)
        assertEquals(25.0, breakdown[1].percentage, 0.0)
    }

    @Test
    fun calculate_returnsIncomeBreakdownSeparatelyFromExpenses() {
        val salary = category(id = 1, name = "Salary", type = TransactionType.INCOME)
        val dividends = category(id = 2, name = "Dividends", type = TransactionType.INCOME)
        val groceries = category(id = 3, name = "Groceries", type = TransactionType.EXPENSE)

        val breakdown = CategoryBreakdownCalculator.calculate(
            transactions = listOf(
                transaction(type = TransactionType.INCOME, amount = 800.0, categoryId = salary.id),
                transaction(type = TransactionType.INCOME, amount = 200.0, categoryId = dividends.id),
                transaction(type = TransactionType.EXPENSE, amount = 500.0, categoryId = groceries.id),
            ),
            categories = listOf(salary, dividends, groceries),
            type = TransactionType.INCOME,
        )

        assertEquals(listOf("Salary", "Dividends"), breakdown.map { it.category.name })
        assertEquals(800.0, breakdown[0].amount, 0.0)
        assertEquals(80.0, breakdown[0].percentage, 0.0)
        assertEquals(200.0, breakdown[1].amount, 0.0)
        assertEquals(20.0, breakdown[1].percentage, 0.0)
    }

    @Test
    fun calculate_usesUncategorizedFallbackForTransactionsWithoutMatchingCategory() {
        val food = category(id = 1, name = "Food", type = TransactionType.EXPENSE)

        val breakdown = CategoryBreakdownCalculator.calculate(
            transactions = listOf(
                transaction(type = TransactionType.EXPENSE, amount = 60.0, categoryId = food.id),
                transaction(type = TransactionType.EXPENSE, amount = 40.0, categoryId = 999),
            ),
            categories = listOf(food),
            type = TransactionType.EXPENSE,
        )

        val uncategorized = breakdown.first { it.category.name == "Uncategorized" }
        assertEquals(0L, uncategorized.category.id)
        assertEquals(TransactionType.EXPENSE, uncategorized.category.type)
        assertEquals(0xFF9E9E9E, uncategorized.category.color)
        assertEquals(40.0, uncategorized.amount, 0.0)
        assertEquals(40.0, uncategorized.percentage, 0.0)
    }

    @Test
    fun calculate_usesUncategorizedFallbackWhenCategoryTypeDoesNotMatchRequestedType() {
        val salary = category(id = 1, name = "Salary", type = TransactionType.INCOME)

        val breakdown = CategoryBreakdownCalculator.calculate(
            transactions = listOf(
                transaction(type = TransactionType.EXPENSE, amount = 75.0, categoryId = salary.id),
            ),
            categories = listOf(salary),
            type = TransactionType.EXPENSE,
        )

        assertEquals(1, breakdown.size)
        assertEquals("Uncategorized", breakdown.single().category.name)
        assertEquals(0L, breakdown.single().category.id)
        assertEquals(TransactionType.EXPENSE, breakdown.single().category.type)
        assertEquals(0xFF9E9E9E, breakdown.single().category.color)
        assertEquals(75.0, breakdown.single().amount, 0.0)
        assertEquals(100.0, breakdown.single().percentage, 0.0)
    }

    private fun category(
        id: Long,
        name: String,
        type: TransactionType,
    ): Category = Category(
        id = id,
        name = name,
        type = type,
    )

    private fun transaction(
        type: TransactionType,
        amount: Double,
        categoryId: Long?,
    ): Transaction = Transaction(
        type = type,
        amount = amount,
        accountId = 1,
        categoryId = categoryId,
        date = 0L,
    )
}
