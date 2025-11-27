package com.roadfam.farminventsof.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roadfam.farminventsof.data.model.Category
import com.roadfam.farminventsof.data.repository.InventoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: InventoryRepository
) : ViewModel() {
    
    val categories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()
    
    private val _editingCategory = MutableStateFlow<Category?>(null)
    val editingCategory: StateFlow<Category?> = _editingCategory.asStateFlow()
    
    fun showAddDialog() {
        _showAddDialog.value = true
    }
    
    fun hideAddDialog() {
        _showAddDialog.value = false
        _editingCategory.value = null
    }
    
    fun editCategory(category: Category) {
        _editingCategory.value = category
        _showAddDialog.value = true
    }
    
    fun saveCategory(name: String, icon: String, color: String) {
        viewModelScope.launch {
            val category = _editingCategory.value
            if (category != null) {
                // Update existing
                repository.updateCategory(
                    category.copy(name = name, icon = icon, color = color)
                )
            } else {
                // Create new
                repository.insertCategory(
                    Category(name = name, icon = icon, color = color)
                )
            }
            hideAddDialog()
        }
    }
    
    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }
}

