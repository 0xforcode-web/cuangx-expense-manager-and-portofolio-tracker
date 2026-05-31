package com.cuangx.finance.core.database.mapper

import com.cuangx.finance.core.database.entity.DebtReceivablePaymentEntity
import com.cuangx.finance.domain.model.DebtReceivablePayment

fun DebtReceivablePaymentEntity.toDomain(): DebtReceivablePayment {
    return DebtReceivablePayment(
        id = id,
        debtId = debtId,
        amount = amount,
        accountId = accountId,
        transactionId = transactionId,
        date = date,
        createdAt = createdAt
    )
}

fun DebtReceivablePayment.toEntity(): DebtReceivablePaymentEntity {
    return DebtReceivablePaymentEntity(
        id = id,
        debtId = debtId,
        amount = amount,
        accountId = accountId,
        transactionId = transactionId,
        date = date,
        createdAt = createdAt
    )
}
