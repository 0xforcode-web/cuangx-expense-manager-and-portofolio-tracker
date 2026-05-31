package com.cuangx.finance.domain.model

data class Holding(
    val id: Long = 0,
    val assetType: AssetType,
    val ticker: String? = null,
    val name: String,
    val quantity: Double,
    val avgBuyPrice: Double,
    val currency: String = "IDR",
    val fundingAccountId: Long? = null,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
