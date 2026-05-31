package com.cuangx.finance.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cuangx.finance.core.database.entity.DividendRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DividendRecordDao {

    @Query("SELECT * FROM dividend_records WHERE holdingId = :holdingId ORDER BY date DESC")
    fun getByHoldingId(holdingId: Long): Flow<List<DividendRecordEntity>>

    @Query("SELECT * FROM dividend_records ORDER BY date DESC")
    fun getAll(): Flow<List<DividendRecordEntity>>

    @Query("SELECT * FROM dividend_records WHERE id = :id")
    suspend fun getById(id: Long): DividendRecordEntity?

    @Query("SELECT * FROM dividend_records WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<DividendRecordEntity>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM dividend_records WHERE date BETWEEN :startDate AND :endDate")
    fun getTotalByDateRange(startDate: Long, endDate: Long): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: DividendRecordEntity): Long

    @Delete
    suspend fun delete(record: DividendRecordEntity)

    @Query("DELETE FROM dividend_records WHERE id = :id")
    suspend fun deleteById(id: Long)
}
