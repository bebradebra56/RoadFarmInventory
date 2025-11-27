package com.roadfam.farminventsof.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roadfam.farminventsof.data.model.InventoryItem
import com.roadfam.farminventsof.data.model.ItemHistory
import com.roadfam.farminventsof.data.model.ItemStatus
import com.roadfam.farminventsof.data.repository.InventoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ItemDetailViewModel(
    private val repository: InventoryRepository
) : ViewModel() {
    
    private val _itemId = MutableStateFlow<Long?>(null)
    
    private val _item = MutableStateFlow<InventoryItem?>(null)
    val item: StateFlow<InventoryItem?> = _item.asStateFlow()
    
    val history: StateFlow<List<ItemHistory>> = _itemId
        .filterNotNull()
        .flatMapLatest { id ->
            repository.getHistoryForItem(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun loadItem(itemId: Long) {
        _itemId.value = itemId
        viewModelScope.launch {
            _item.value = repository.getItemById(itemId)
        }
    }
    
    fun updateItemStatus(newStatus: ItemStatus) {
        viewModelScope.launch {
            _item.value?.let { currentItem ->
                val updatedItem = currentItem.copy(status = newStatus)
                repository.updateItem(updatedItem)
                _item.value = updatedItem
            }
        }
    }
    
    fun deleteItem() {
        viewModelScope.launch {
            _item.value?.let { repository.deleteItem(it) }
        }
    }
}

