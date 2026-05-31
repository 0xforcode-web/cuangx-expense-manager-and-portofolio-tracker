package com.cuangx.finance.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dividend_records",
    foreignKeys = [
        ForeignKey(
            entity = HoldingEntity::class,
            parentColumns = ["id"],
            childColumns = ["holdingId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("holdingId")]
)
data class DividendRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val holdingId: Long,
    val amount: Double,
    val accountId: Long? = null,
    val date: Long,
    val createdAt: Long = System.currentTimeMillis()
)
