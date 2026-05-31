package com.cuangx.finance.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holdings")
data class HoldingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetType: String,
    val ticker: String? = null,
    val name: String,
    val quantity: Double,
    val avgBuyPrice: Double,
    val currency: String = "IDR",
    val fundingAccountId: Long? = null,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
