package com.cuangx.finance.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_cache")
data class PriceCacheEntity(
    @PrimaryKey
    val ticker: String,
    val price: Double,
    val currency: String,
    val name: String = "",
    val changePercent: Double = 0.0,
    val lastUpdated: Long
)
