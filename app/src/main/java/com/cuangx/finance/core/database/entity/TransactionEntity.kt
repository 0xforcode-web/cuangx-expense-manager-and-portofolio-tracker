package com.cuangx.finance.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["toAccountId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("accountId"),
        Index("toAccountId"),
        Index("categoryId"),
        Index("date")
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,
    val amount: Double,
    val accountId: Long,
    val toAccountId: Long? = null,
    val categoryId: Long? = null,
    val date: Long,
    val note: String = "",
    val photoUri: String? = null,
    val isBookmarked: Boolean = false,
    val linkedHoldingId: Long? = null,
    val linkedDividendId: Long? = null,
    val linkedDebtId: Long? = null,
    val linkedReceivableId: Long? = null,
    val source: String = "EXPENSE",
    val createdAt: Long = System.currentTimeMillis()
)
