package com.cuangx.finance.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cuangx.finance.core.database.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts WHERE isArchived = 0 ORDER BY sortOrder ASC, name ASC")
    fun getAllActive(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts ORDER BY sortOrder ASC, name ASC")
    fun getAll(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    fun getById(id: Long): Flow<AccountEntity?>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getByIdOnce(id: Long): AccountEntity?

    @Query("SELECT SUM(balance) FROM accounts WHERE isArchived = 0")
    fun getTotalBalance(): Flow<Double?>

    @Query("UPDATE accounts SET balance = balance + :amount WHERE id = :id")
    suspend fun updateBalance(id: Long, amount: Double)

    @Query("SELECT balance FROM accounts WHERE id = :id")
    suspend fun getBalance(id: Long): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AccountEntity): Long

    @Update
    suspend fun update(account: AccountEntity)

    @Delete
    suspend fun delete(account: AccountEntity)

    @Query("UPDATE accounts SET isArchived = 1 WHERE id = :id")
    suspend fun archive(id: Long)

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun getCount(): Int
}
