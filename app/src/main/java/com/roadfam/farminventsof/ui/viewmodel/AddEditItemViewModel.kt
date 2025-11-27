package com.roadfam.farminventsof.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roadfam.farminventsof.data.model.Category
import com.roadfam.farminventsof.data.model.InventoryItem
import com.roadfam.farminventsof.data.model.ItemStatus
import com.roadfam.farminventsof.data.repository.InventoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ItemFormState(
    val name: String = "",
    val categoryId: Long? = null,
    val quantity: Int = 1,
    val status: ItemStatus = ItemStatus.WORKING,
    val note: String = "",
    val nameError: String? = null,
    val categoryError: String? = null
)

class AddEditItemViewModel(
    private val repository: InventoryRepository
) : ViewModel() {
    
    private val _formState = MutableStateFlow(ItemFormState())
    val formState: StateFlow<ItemFormState> = _formState.asStateFlow()
    
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()
    
    private val _currentItemId = MutableStateFlow<Long?>(null)
    
    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()
    
    val categories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun loadItem(itemId: Long) {
        _isEditMode.value = true
        _currentItemId.value = itemId
        viewModelScope.launch {
            repository.getItemById(itemId)?.let { item ->
                _formState.value = ItemFormState(
                    name = item.name,
                    categoryId = item.categoryId,
                    quantity = item.quantity,
                    status = item.status,
                    note = item.note
                )
            }
        }
    }
    
    fun updateName(name: String) {
        _formState.value = _formState.value.copy(
            name = name,
            nameError = if (name.isBlank()) "Name is required" else null
        )
    }
    
    fun updateCategory(categoryId: Long) {
        _formState.value = _formState.value.copy(
            categoryId = categoryId,
            categoryError = null
        )
    }
    
    fun updateQuantity(quantity: Int) {
        _formState.value = _formState.value.copy(
            quantity = quantity.coerceAtLeast(0)
        )
    }
    
    fun updateStatus(status: ItemStatus) {
        _formState.value = _formState.value.copy(status = status)
    }
    
    fun updateNote(note: String) {
        _formState.value = _formState.value.copy(note = note)
    }
    
    fun saveItem() {
        val state = _formState.value
        
        // Validate
        val nameError = if (state.name.isBlank()) "Name is required" else null
        val categoryError = if (state.categoryId == null) "Category is required" else null
        
        if (nameError != null || categoryError != null) {
            _formState.value = state.copy(
                nameError = nameError,
                categoryError = categoryError
            )
            return
        }
        
        viewModelScope.launch {
            try {
                if (_isEditMode.value && _currentItemId.value != null) {
                    // Update existing item
                    val updatedItem = InventoryItem(
                        id = _currentItemId.value!!,
                        name = state.name,
                        categoryId = state.categoryId!!,
                        quantity = state.quantity,
                        status = state.status,
                        note = state.note
                    )
                    repository.updateItem(updatedItem)
                } else {
                    // Create new item
                    val newItem = InventoryItem(
                        name = state.name,
                        categoryId = state.categoryId!!,
                        quantity = state.quantity,
                        status = state.status,
                        note = state.note
                    )
                    repository.insertItem(newItem)
                }
                _saveSuccess.value = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
}

