package com.cuangx.finance.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "debt_receivable_payments",
    foreignKeys = [
        ForeignKey(
            entity = DebtReceivableEntity::class,
            parentColumns = ["id"],
            childColumns = ["debtId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("debtId"),
        Index("accountId")
    ]
)
data class DebtReceivablePaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val debtId: Long,
    val amount: Double,
    val accountId: Long,
    val transactionId: Long? = null,
    val date: Long,
    val createdAt: Long = System.currentTimeMillis()
)
