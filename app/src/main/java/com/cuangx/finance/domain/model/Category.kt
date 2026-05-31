package com.cuangx.finance.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val type: TransactionType,
    val icon: String = "ic_category",
    val color: Long = 0xFF2196F3,
    val parentId: Long? = null,
    val sortOrder: Int = 0
)
