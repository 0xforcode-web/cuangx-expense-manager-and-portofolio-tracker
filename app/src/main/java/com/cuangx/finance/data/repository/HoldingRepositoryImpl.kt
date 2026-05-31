package com.cuangx.finance.data.repository

import com.cuangx.finance.core.database.dao.HoldingDao
import com.cuangx.finance.core.database.mapper.toDomain
import com.cuangx.finance.core.database.mapper.toEntity
import com.cuangx.finance.domain.model.Holding
import com.cuangx.finance.domain.repository.HoldingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HoldingRepositoryImpl @Inject constructor(
    private val holdingDao: HoldingDao
) : HoldingRepository {

    override fun getAll(): Flow<List<Holding>> {
        return holdingDao.getAll().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getByAssetType(assetType: String): Flow<List<Holding>> {
        return holdingDao.getByAssetType(assetType).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getById(id: Long): Flow<Holding?> {
        return holdingDao.getById(id).map { it?.toDomain() }
    }

    override suspend fun getByIdOnce(id: Long): Holding? {
        return holdingDao.getByIdOnce(id)?.toDomain()
    }

    override suspend fun getAllTickers(): List<String> {
        return holdingDao.getAllTickers()
    }

    override suspend fun getByTicker(ticker: String): Holding? {
        return holdingDao.getByTicker(ticker)?.toDomain()
    }

    override suspend fun insert(holding: Holding): Long {
        return holdingDao.insert(holding.toEntity())
    }

    override suspend fun update(holding: Holding) {
        holdingDao.update(holding.toEntity())
    }

    override suspend fun delete(holding: Holding) {
        holdingDao.delete(holding.toEntity())
    }

    override suspend fun deleteById(id: Long) {
        holdingDao.deleteById(id)
    }

    override suspend fun getCount(): Int {
        return holdingDao.getCount()
    }
}
