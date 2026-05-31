package com.cuangx.finance.data.repository

import com.cuangx.finance.core.database.dao.CategoryDao
import com.cuangx.finance.core.database.mapper.toDomain
import com.cuangx.finance.core.database.mapper.toEntity
import com.cuangx.finance.domain.model.Category
import com.cuangx.finance.domain.model.TransactionType
import com.cuangx.finance.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAll(): Flow<List<Category>> {
        return categoryDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getByType(type: TransactionType): Flow<List<Category>> {
        return categoryDao.getByType(type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllParents(): Flow<List<Category>> {
        return categoryDao.getAllParents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSubCategories(parentId: Long): Flow<List<Category>> {
        return categoryDao.getSubCategories(parentId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getById(id: Long): Flow<Category?> {
        return categoryDao.getById(id).map { it?.toDomain() }
    }

    override suspend fun getByIdOnce(id: Long): Category? {
        return categoryDao.getByIdOnce(id)?.toDomain()
    }

    override suspend fun getByNameAndType(name: String, type: TransactionType): Category? {
        return categoryDao.getByNameAndType(name, type.name)?.toDomain()
    }

    override suspend fun insert(category: Category): Long {
        return categoryDao.insert(category.toEntity())
    }

    override suspend fun update(category: Category) {
        categoryDao.update(category.toEntity())
    }

    override suspend fun delete(category: Category) {
        categoryDao.delete(category.toEntity())
    }

    override suspend fun getCount(): Int {
        return categoryDao.getCount()
    }
}
