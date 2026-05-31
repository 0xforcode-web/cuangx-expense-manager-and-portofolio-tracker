package com.cuangx.finance.core.database.mapper

import com.cuangx.finance.core.database.entity.BudgetEntity
import com.cuangx.finance.domain.model.Budget
import com.cuangx.finance.domain.model.BudgetPeriod

fun BudgetEntity.toDomain(): Budget {
    return Budget(
        id = id,
        categoryId = categoryId,
        amount = amount,
        period = BudgetPeriod.valueOf(period),
        startDate = startDate,
        isActive = isActive
    )
}

fun Budget.toEntity(): BudgetEntity {
    return BudgetEntity(
        id = id,
        categoryId = categoryId,
        amount = amount,
        period = period.name,
        startDate = startDate,
        isActive = isActive
    )
}
