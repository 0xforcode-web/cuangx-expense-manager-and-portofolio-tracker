package com.cuangx.finance.core.database.mapper

import com.cuangx.finance.core.database.entity.JournalEntryEntity
import com.cuangx.finance.domain.model.AssetType
import com.cuangx.finance.domain.model.JournalAction
import com.cuangx.finance.domain.model.JournalEntry

fun JournalEntryEntity.toDomain(): JournalEntry {
    return JournalEntry(
        id = id,
        action = JournalAction.valueOf(action),
        assetType = AssetType.valueOf(assetType),
        ticker = ticker,
        name = name,
        quantity = quantity,
        price = price,
        fee = fee,
        accountId = accountId,
        transactionId = transactionId,
        reason = reason,
        tags = tags,
        date = date,
        note = note,
        createdAt = createdAt
    )
}

fun JournalEntry.toEntity(): JournalEntryEntity {
    return JournalEntryEntity(
        id = id,
        action = action.name,
        assetType = assetType.name,
        ticker = ticker,
        name = name,
        quantity = quantity,
        price = price,
        fee = fee,
        accountId = accountId,
        transactionId = transactionId,
        reason = reason,
        tags = tags,
        date = date,
        note = note,
        createdAt = createdAt
    )
}
