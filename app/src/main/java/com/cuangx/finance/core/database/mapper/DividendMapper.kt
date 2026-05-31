package com.cuangx.finance.core.database.mapper

import com.cuangx.finance.core.database.entity.DividendRecordEntity
import com.cuangx.finance.domain.model.DividendRecord

fun DividendRecordEntity.toDomain(): DividendRecord {
    return DividendRecord(
        id = id,
        holdingId = holdingId,
        amount = amount,
        accountId = accountId,
        date = date,
        createdAt = createdAt
    )
}

fun DividendRecord.toEntity(): DividendRecordEntity {
    return DividendRecordEntity(
        id = id,
        holdingId = holdingId,
        amount = amount,
        accountId = accountId,
        date = date,
        createdAt = createdAt
    )
}
