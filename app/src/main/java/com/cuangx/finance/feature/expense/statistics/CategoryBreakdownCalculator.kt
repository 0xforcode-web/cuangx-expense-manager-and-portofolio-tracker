package com.cuangx.finance.feature.expense.statistics

import com.cuangx.finance.domain.model.Category
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionType

object CategoryBreakdownCalculator {

    fun calculate(
        transactions: List<Transaction>,
        categories: List<Category>,
        type: TransactionType,
    ): List<CategoryExpense> {
        val selectedTransactions = transactions.filter { it.type == type }
        val total = selectedTransactions.sumOf { it.amount }

        if (total == 0.0) {
            return emptyList()
        }

        val categoriesById = categories
            .filter { it.type == type }
            .associateBy { it.id }
        val uncategorized = Category(
            id = 0,
            name = "Uncategorized",
            type = type,
            color = 0xFF9E9E9E,
        )

        return selectedTransactions
            .groupBy { transaction ->
                categoriesById[transaction.categoryId] ?: uncategorized
            }
            .map { (category, categoryTransactions) ->
                val amount = categoryTransactions.sumOf { it.amount }
                CategoryExpense(
                    category = category,
                    amount = amount,
                    percentage = (amount / total) * 100,
                )
            }
            .sortedByDescending { it.amount }
    }
}
