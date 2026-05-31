package com.cuangx.finance.feature.expense.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Category
import com.cuangx.finance.domain.model.TransactionType
import com.cuangx.finance.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryWithChildren(
    val category: Category,
    val children: List<Category> = emptyList()
)

data class CategoryListUiState(
    val expenseCategories: List<CategoryWithChildren> = emptyList(),
    val incomeCategories: List<CategoryWithChildren> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _expandedCategories = MutableStateFlow<Set<Long>>(emptySet())
    val expandedCategories: StateFlow<Set<Long>> = _expandedCategories.asStateFlow()

    val uiState: StateFlow<CategoryListUiState> = combine(
        categoryRepository.getAll(),
        _expandedCategories
    ) { allCategories, expanded ->
        val expenseParents = allCategories.filter { it.type == TransactionType.EXPENSE && it.parentId == null }
        val incomeParents = allCategories.filter { it.type == TransactionType.INCOME && it.parentId == null }

        val expenseWithChildren = expenseParents.map { parent ->
            CategoryWithChildren(
                category = parent,
                children = allCategories.filter { it.parentId == parent.id }
            )
        }

        val incomeWithChildren = incomeParents.map { parent ->
            CategoryWithChildren(
                category = parent,
                children = allCategories.filter { it.parentId == parent.id }
            )
        }

        CategoryListUiState(
            expenseCategories = expenseWithChildren,
            incomeCategories = incomeWithChildren,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CategoryListUiState()
    )

    fun toggleExpanded(categoryId: Long) {
        _expandedCategories.value = if (_expandedCategories.value.contains(categoryId)) {
            _expandedCategories.value - categoryId
        } else {
            _expandedCategories.value + categoryId
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.delete(category)
        }
    }
}
