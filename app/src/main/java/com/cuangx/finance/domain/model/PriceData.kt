package com.cuangx.finance.domain.model

data class PriceData(
    val ticker: String,
    val price: Double,
    val currency: String,
    val name: String = "",
    val changePercent: Double = 0.0,
    val lastUpdated: Long
)
