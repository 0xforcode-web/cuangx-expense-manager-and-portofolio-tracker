package com.cuangx.finance.core.database.mapper

import com.cuangx.finance.core.database.entity.PriceCacheEntity
import com.cuangx.finance.domain.model.PriceData

fun PriceCacheEntity.toDomain(): PriceData {
    return PriceData(
        ticker = ticker,
        price = price,
        currency = currency,
        name = name,
        changePercent = changePercent,
        lastUpdated = lastUpdated
    )
}

fun PriceData.toEntity(): PriceCacheEntity {
    return PriceCacheEntity(
        ticker = ticker,
        price = price,
        currency = currency,
        name = name,
        changePercent = changePercent,
        lastUpdated = lastUpdated
    )
}
