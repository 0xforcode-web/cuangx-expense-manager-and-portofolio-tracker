package com.cuangx.finance.feature.expense.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Category
import com.cuangx.finance.domain.model.TransactionType
import com.cuangx.finance.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryListUiState(
    val expenseCategories: List<Category> = emptyList(),
    val incomeCategories: List<Category> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val uiState: StateFlow<CategoryListUiState> = combine(
        categoryRepository.getByType(TransactionType.EXPENSE),
        categoryRepository.getByType(TransactionType.INCOME)
    ) { expense, income ->
        CategoryListUiState(
            expenseCategories = expense,
            incomeCategories = income,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CategoryListUiState()
    )

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.delete(category)
        }
    }
}
