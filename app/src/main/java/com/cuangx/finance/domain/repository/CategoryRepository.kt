package com.cuangx.finance.domain.repository

import com.cuangx.finance.domain.model.Category
import com.cuangx.finance.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAll(): Flow<List<Category>>
    fun getByType(type: TransactionType): Flow<List<Category>>
    fun getAllParents(): Flow<List<Category>>
    fun getSubCategories(parentId: Long): Flow<List<Category>>
    fun getById(id: Long): Flow<Category?>
    suspend fun getByIdOnce(id: Long): Category?
    suspend fun getByNameAndType(name: String, type: TransactionType): Category?
    suspend fun insert(category: Category): Long
    suspend fun update(category: Category)
    suspend fun delete(category: Category)
    suspend fun getCount(): Int
}
