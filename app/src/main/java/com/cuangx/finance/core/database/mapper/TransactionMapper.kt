package com.cuangx.finance.core.database.mapper

import com.cuangx.finance.core.database.entity.TransactionEntity
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionSource
import com.cuangx.finance.domain.model.TransactionType

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        type = TransactionType.valueOf(type),
        amount = amount,
        accountId = accountId,
        toAccountId = toAccountId,
        categoryId = categoryId,
        date = date,
        note = note,
        photoUri = photoUri,
        isBookmarked = isBookmarked,
        linkedHoldingId = linkedHoldingId,
        linkedDividendId = linkedDividendId,
        linkedDebtId = linkedDebtId,
        linkedReceivableId = linkedReceivableId,
        source = TransactionSource.valueOf(source),
        createdAt = createdAt
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        type = type.name,
        amount = amount,
        accountId = accountId,
        toAccountId = toAccountId,
        categoryId = categoryId,
        date = date,
        note = note,
        photoUri = photoUri,
        isBookmarked = isBookmarked,
        linkedHoldingId = linkedHoldingId,
        linkedDividendId = linkedDividendId,
        linkedDebtId = linkedDebtId,
        linkedReceivableId = linkedReceivableId,
        source = source.name,
        createdAt = createdAt
    )
}
