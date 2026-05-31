package com.cuangx.finance.core.database.mapper

import com.cuangx.finance.core.database.entity.RecurringTransactionEntity
import com.cuangx.finance.domain.model.Frequency
import com.cuangx.finance.domain.model.RecurringTransaction
import com.cuangx.finance.domain.model.TransactionType

fun RecurringTransactionEntity.toDomain(): RecurringTransaction {
    return RecurringTransaction(
        id = id,
        type = TransactionType.valueOf(type),
        amount = amount,
        accountId = accountId,
        toAccountId = toAccountId,
        categoryId = categoryId,
        frequency = Frequency.valueOf(frequency),
        nextDate = nextDate,
        isActive = isActive,
        note = note
    )
}

fun RecurringTransaction.toEntity(): RecurringTransactionEntity {
    return RecurringTransactionEntity(
        id = id,
        type = type.name,
        amount = amount,
        accountId = accountId,
        toAccountId = toAccountId,
        categoryId = categoryId,
        frequency = frequency.name,
        nextDate = nextDate,
        isActive = isActive,
        note = note
    )
}
