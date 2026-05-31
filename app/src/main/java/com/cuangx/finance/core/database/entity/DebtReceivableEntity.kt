package com.cuangx.finance.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts_receivables")
data class DebtReceivableEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,
    val partyName: String,
    val originalAmount: Double,
    val remainingAmount: Double,
    val currency: String = "IDR",
    val interestRate: Double = 0.0,
    val dateCreated: Long,
    val dueDate: Long? = null,
    val status: String = "ACTIVE",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
