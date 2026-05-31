package com.cuangx.finance.core.database.mapper

import com.cuangx.finance.core.database.entity.HoldingEntity
import com.cuangx.finance.domain.model.AssetType
import com.cuangx.finance.domain.model.Holding

fun HoldingEntity.toDomain(): Holding {
    return Holding(
        id = id,
        assetType = AssetType.valueOf(assetType),
        ticker = ticker,
        name = name,
        quantity = quantity,
        avgBuyPrice = avgBuyPrice,
        currency = currency,
        fundingAccountId = fundingAccountId,
        note = note,
        createdAt = createdAt
    )
}

fun Holding.toEntity(): HoldingEntity {
    return HoldingEntity(
        id = id,
        assetType = assetType.name,
        ticker = ticker,
        name = name,
        quantity = quantity,
        avgBuyPrice = avgBuyPrice,
        currency = currency,
        fundingAccountId = fundingAccountId,
        note = note,
        createdAt = createdAt
    )
}
