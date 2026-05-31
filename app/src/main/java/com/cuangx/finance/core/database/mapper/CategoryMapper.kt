package com.cuangx.finance.core.database.mapper

import com.cuangx.finance.core.database.entity.CategoryEntity
import com.cuangx.finance.domain.model.Category
import com.cuangx.finance.domain.model.TransactionType

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        type = TransactionType.valueOf(type),
        icon = icon,
        color = color,
        parentId = parentId,
        sortOrder = sortOrder
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        type = type.name,
        icon = icon,
        color = color,
        parentId = parentId,
        sortOrder = sortOrder
    )
}
