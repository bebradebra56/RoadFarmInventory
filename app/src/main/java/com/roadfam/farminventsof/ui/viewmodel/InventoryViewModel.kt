package com.roadfam.farminventsof.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roadfam.farminventsof.data.model.Category
import com.roadfam.farminventsof.data.model.InventoryItem
import com.roadfam.farminventsof.data.model.ItemStatus
import com.roadfam.farminventsof.data.repository.InventoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val repository: InventoryRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedStatus = MutableStateFlow<ItemStatus?>(null)
    val selectedStatus: StateFlow<ItemStatus?> = _selectedStatus.asStateFlow()
    
    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()
    
    val categories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val allItems: StateFlow<List<InventoryItem>> = repository.getAllItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val filteredItems: StateFlow<List<InventoryItem>> = combine(
        allItems,
        searchQuery,
        selectedStatus,
        selectedCategoryId
    ) { items, query, status, categoryId ->
        items.filter { item ->
            val matchesSearch = query.isEmpty() || 
                item.name.contains(query, ignoreCase = true)
            val matchesStatus = status == null || item.status == status
            val matchesCategory = categoryId == null || item.categoryId == categoryId
            
            matchesSearch && matchesStatus && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val totalItemCount: StateFlow<Int> = repository.getTotalItemCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    
    val workingItemsCount: StateFlow<Int> = repository.getItemCountByStatus(ItemStatus.WORKING)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    
    val needsRepairCount: StateFlow<Int> = repository.getItemCountByStatus(ItemStatus.NEEDS_REPAIR)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    
    val brokenItemsCount: StateFlow<Int> = repository.getItemCountByStatus(ItemStatus.BROKEN)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun setStatusFilter(status: ItemStatus?) {
        _selectedStatus.value = status
    }
    
    fun setCategoryFilter(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }
    
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedStatus.value = null
        _selectedCategoryId.value = null
    }
    
    fun deleteItem(item: InventoryItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }
    
    fun getCategoryById(id: Long): Category? {
        return categories.value.find { it.id == id }
    }
}

