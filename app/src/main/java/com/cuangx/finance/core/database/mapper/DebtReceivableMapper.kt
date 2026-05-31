package com.cuangx.finance.core.database.mapper

import com.cuangx.finance.core.database.entity.DebtReceivableEntity
import com.cuangx.finance.domain.model.DebtReceivable
import com.cuangx.finance.domain.model.DebtReceivableType
import com.cuangx.finance.domain.model.DebtStatus

fun DebtReceivableEntity.toDomain(): DebtReceivable {
    return DebtReceivable(
        id = id,
        type = DebtReceivableType.valueOf(type),
        partyName = partyName,
        originalAmount = originalAmount,
        remainingAmount = remainingAmount,
        currency = currency,
        interestRate = interestRate,
        dateCreated = dateCreated,
        dueDate = dueDate,
        status = DebtStatus.valueOf(status),
        note = note,
        createdAt = createdAt
    )
}

fun DebtReceivable.toEntity(): DebtReceivableEntity {
    return DebtReceivableEntity(
        id = id,
        type = type.name,
        partyName = partyName,
        originalAmount = originalAmount,
        remainingAmount = remainingAmount,
        currency = currency,
        interestRate = interestRate,
        dateCreated = dateCreated,
        dueDate = dueDate,
        status = status.name,
        note = note,
        createdAt = createdAt
    )
}
