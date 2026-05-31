package com.cuangx.finance.data.repository

import com.cuangx.finance.core.database.dao.DividendRecordDao
import com.cuangx.finance.core.database.mapper.toDomain
import com.cuangx.finance.core.database.mapper.toEntity
import com.cuangx.finance.domain.model.DividendRecord
import com.cuangx.finance.domain.repository.DividendRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DividendRepositoryImpl @Inject constructor(
    private val dividendRecordDao: DividendRecordDao
) : DividendRepository {

    override fun getByHoldingId(holdingId: Long): Flow<List<DividendRecord>> {
        return dividendRecordDao.getByHoldingId(holdingId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAll(): Flow<List<DividendRecord>> {
        return dividendRecordDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: Long): DividendRecord? {
        return dividendRecordDao.getById(id)?.toDomain()
    }

    override fun getByDateRange(startDate: Long, endDate: Long): Flow<List<DividendRecord>> {
        return dividendRecordDao.getByDateRange(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTotalByDateRange(startDate: Long, endDate: Long): Flow<Double> {
        return dividendRecordDao.getTotalByDateRange(startDate, endDate)
    }

    override suspend fun insert(record: DividendRecord): Long {
        return dividendRecordDao.insert(record.toEntity())
    }

    override suspend fun delete(record: DividendRecord) {
        dividendRecordDao.delete(record.toEntity())
    }

    override suspend fun deleteById(id: Long) {
        dividendRecordDao.deleteById(id)
    }
}
