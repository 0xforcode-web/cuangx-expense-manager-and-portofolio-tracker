package com.cuangx.finance.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cuangx.finance.core.database.entity.HoldingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HoldingDao {

    @Query("SELECT * FROM holdings ORDER BY name ASC")
    fun getAll(): Flow<List<HoldingEntity>>

    @Query("SELECT * FROM holdings WHERE assetType = :assetType ORDER BY name ASC")
    fun getByAssetType(assetType: String): Flow<List<HoldingEntity>>

    @Query("SELECT * FROM holdings WHERE id = :id")
    fun getById(id: Long): Flow<HoldingEntity?>

    @Query("SELECT * FROM holdings WHERE id = :id")
    suspend fun getByIdOnce(id: Long): HoldingEntity?

    @Query("SELECT DISTINCT ticker FROM holdings WHERE ticker IS NOT NULL")
    suspend fun getAllTickers(): List<String>

    @Query("SELECT * FROM holdings WHERE ticker = :ticker LIMIT 1")
    suspend fun getByTicker(ticker: String): HoldingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(holding: HoldingEntity): Long

    @Update
    suspend fun update(holding: HoldingEntity)

    @Delete
    suspend fun delete(holding: HoldingEntity)

    @Query("DELETE FROM holdings WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM holdings")
    suspend fun getCount(): Int
}
