package com.cuangx.finance.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String,
    val icon: String = "ic_category",
    val color: Long = 0xFF2196F3,
    val parentId: Long? = null,
    val sortOrder: Int = 0
)
