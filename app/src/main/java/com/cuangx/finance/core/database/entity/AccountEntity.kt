package com.cuangx.finance.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String,
    val balance: Double = 0.0,
    val currency: String = "IDR",
    val icon: String = "ic_wallet",
    val color: Long = 0xFF4CAF50,
    val creditLimit: Double? = null,
    val settlementDay: Int? = null,
    val isArchived: Boolean = false,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
