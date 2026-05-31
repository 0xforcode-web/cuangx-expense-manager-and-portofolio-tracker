package com.cuangx.finance.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "journal_entries",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("accountId"),
        Index("ticker"),
        Index("date"),
        Index("action")
    ]
)
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val action: String,
    val assetType: String,
    val ticker: String? = null,
    val name: String,
    val quantity: Double,
    val price: Double,
    val fee: Double = 0.0,
    val accountId: Long,
    val transactionId: Long? = null,
    val reason: String = "",
    val tags: String = "",
    val date: Long,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
