package com.roadfam.farminventsof.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roadfam.farminventsof.data.model.Category
import com.roadfam.farminventsof.data.model.InventoryItem
import com.roadfam.farminventsof.data.model.ItemStatus
import com.roadfam.farminventsof.data.repository.InventoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CategoryStatistic(
    val category: Category,
    val itemCount: Int,
    val percentage: Float
)

data class StatusStatistic(
    val status: ItemStatus,
    val count: Int,
    val percentage: Float
)

class AnalyticsViewModel(
    private val repository: InventoryRepository
) : ViewModel() {
    
    private val categories = repository.getAllCategories()
    private val allItems = repository.getAllItems()
    
    val categoryStatistics: StateFlow<List<CategoryStatistic>> = combine(
        categories,
        allItems
    ) { cats, items ->
        val total = items.size.toFloat()
        if (total == 0f) return@combine emptyList()
        
        cats.map { category ->
            val count = items.count { it.categoryId == category.id }
            CategoryStatistic(
                category = category,
                itemCount = count,
                percentage = (count / total) * 100f
            )
        }.sortedByDescending { it.itemCount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val statusStatistics: StateFlow<List<StatusStatistic>> = allItems.map { items ->
        val total = items.size.toFloat()
        if (total == 0f) return@map emptyList()
        
        ItemStatus.values().map { status ->
            val count = items.count { it.status == status }
            StatusStatistic(
                status = status,
                count = count,
                percentage = (count / total) * 100f
            )
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
    
    // Calculate items added in the last 7 days
    val itemsAddedThisWeek: StateFlow<Int> = allItems.map { items ->
        val weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        items.count { it.createdAt >= weekAgo }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    
    // Calculate items added in the last 30 days
    val itemsAddedThisMonth: StateFlow<Int> = allItems.map { items ->
        val monthAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        items.count { it.createdAt >= monthAgo }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
}

