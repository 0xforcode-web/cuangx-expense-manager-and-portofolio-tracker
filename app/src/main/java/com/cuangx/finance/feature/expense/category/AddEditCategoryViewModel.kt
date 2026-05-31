package com.cuangx.finance.feature.expense.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.domain.model.Category
import com.cuangx.finance.domain.model.TransactionType
import com.cuangx.finance.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditCategoryUiState(
    val name: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val icon: String = "ic_category",
    val color: Long = 0xFF2196F3,
    val parentId: Long? = null,
    val parentName: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false
)

sealed class AddEditCategoryEvent {
    data object SaveSuccess : AddEditCategoryEvent()
    data class ShowError(val message: String) : AddEditCategoryEvent()
}

@HiltViewModel
class AddEditCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditCategoryUiState())
    val uiState: StateFlow<AddEditCategoryUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddEditCategoryEvent>()
    val event: SharedFlow<AddEditCategoryEvent> = _event.asSharedFlow()

    private var editingCategoryId: Long = 0

    fun loadCategory(categoryId: Long) {
        viewModelScope.launch {
            val category = categoryRepository.getByIdOnce(categoryId) ?: return@launch
            editingCategoryId = category.id

            var parentName = ""
            if (category.parentId != null) {
                val parent = categoryRepository.getByIdOnce(category.parentId)
                parentName = parent?.name ?: ""
            }

            _uiState.value = _uiState.value.copy(
                name = category.name,
                type = category.type,
                icon = category.icon,
                color = category.color,
                parentId = category.parentId,
                parentName = parentName,
                isEditing = true
            )
        }
    }

    fun loadParentCategory(parentId: Long) {
        viewModelScope.launch {
            val parent = categoryRepository.getByIdOnce(parentId) ?: return@launch
            _uiState.value = _uiState.value.copy(
                parentId = parent.id,
                parentName = parent.name,
                type = parent.type,
                color = parent.color
            )
        }
    }

    fun updateName(name: String) { _uiState.value = _uiState.value.copy(name = name) }
    fun updateType(type: TransactionType) { _uiState.value = _uiState.value.copy(type = type) }
    fun updateIcon(icon: String) { _uiState.value = _uiState.value.copy(icon = icon) }
    fun updateColor(color: Long) { _uiState.value = _uiState.value.copy(color = color) }

    fun save() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            viewModelScope.launch { _event.emit(AddEditCategoryEvent.ShowError("Nama kategori tidak boleh kosong")) }
            return
        }

        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val category = Category(
                    id = if (state.isEditing) editingCategoryId else 0,
                    name = state.name,
                    type = state.type,
                    icon = state.icon,
                    color = state.color,
                    parentId = state.parentId
                )

                if (state.isEditing) {
                    categoryRepository.update(category)
                } else {
                    categoryRepository.insert(category)
                }

                _event.emit(AddEditCategoryEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiState.value = state.copy(isSaving = false)
                _event.emit(AddEditCategoryEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }
}
