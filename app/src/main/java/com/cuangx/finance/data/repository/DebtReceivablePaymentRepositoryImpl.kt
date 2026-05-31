package com.cuangx.finance.data.repository

import com.cuangx.finance.core.database.dao.DebtReceivablePaymentDao
import com.cuangx.finance.core.database.mapper.toDomain
import com.cuangx.finance.core.database.mapper.toEntity
import com.cuangx.finance.domain.model.DebtReceivablePayment
import com.cuangx.finance.domain.repository.DebtReceivablePaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebtReceivablePaymentRepositoryImpl @Inject constructor(
    private val paymentDao: DebtReceivablePaymentDao
) : DebtReceivablePaymentRepository {

    override fun getByDebtId(debtId: Long): Flow<List<DebtReceivablePayment>> {
        return paymentDao.getByDebtId(debtId).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getAll(): Flow<List<DebtReceivablePayment>> {
        return paymentDao.getAll().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getById(id: Long): DebtReceivablePayment? {
        return paymentDao.getById(id)?.toDomain()
    }

    override suspend fun getTotalPaidByDebtId(debtId: Long): Double {
        return paymentDao.getTotalPaidByDebtId(debtId)
    }

    override suspend fun insert(payment: DebtReceivablePayment): Long {
        return paymentDao.insert(payment.toEntity())
    }

    override suspend fun updateTransactionId(id: Long, transactionId: Long) {
        paymentDao.updateTransactionId(id, transactionId)
    }

    override suspend fun delete(payment: DebtReceivablePayment) {
        paymentDao.delete(payment.toEntity())
    }

    override suspend fun deleteById(id: Long) {
        paymentDao.deleteById(id)
    }
}
