package com.cuangx.finance.core.database.mapper

import com.cuangx.finance.core.database.entity.AccountEntity
import com.cuangx.finance.domain.model.Account
import com.cuangx.finance.domain.model.AccountType

fun AccountEntity.toDomain(): Account {
    return Account(
        id = id,
        name = name,
        type = AccountType.valueOf(type),
        balance = balance,
        currency = currency,
        icon = icon,
        color = color,
        creditLimit = creditLimit,
        settlementDay = settlementDay,
        isArchived = isArchived,
        sortOrder = sortOrder,
        createdAt = createdAt
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        name = name,
        type = type.name,
        balance = balance,
        currency = currency,
        icon = icon,
        color = color,
        creditLimit = creditLimit,
        settlementDay = settlementDay,
        isArchived = isArchived,
        sortOrder = sortOrder,
        createdAt = createdAt
    )
}
