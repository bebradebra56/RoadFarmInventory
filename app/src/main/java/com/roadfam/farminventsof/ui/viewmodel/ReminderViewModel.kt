package com.roadfam.farminventsof.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roadfam.farminventsof.data.model.Reminder
import com.roadfam.farminventsof.data.model.ReminderFrequency
import com.roadfam.farminventsof.data.repository.InventoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val repository: InventoryRepository
) : ViewModel() {
    
    val activeReminders: StateFlow<List<Reminder>> = repository.getActiveReminders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val completedReminders: StateFlow<List<Reminder>> = repository.getCompletedReminders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()
    
    fun showAddDialog() {
        _showAddDialog.value = true
    }
    
    fun hideAddDialog() {
        _showAddDialog.value = false
    }
    
    fun addReminder(
        title: String,
        description: String,
        date: Long,
        frequency: ReminderFrequency
    ) {
        viewModelScope.launch {
            val reminder = Reminder(
                title = title,
                description = description,
                date = date,
                frequency = frequency
            )
            repository.insertReminder(reminder)
            hideAddDialog()
        }
    }
    
    fun completeReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.updateReminder(
                reminder.copy(isCompleted = true)
            )
        }
    }
    
    fun uncompleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.updateReminder(
                reminder.copy(isCompleted = false)
            )
        }
    }
    
    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }
}

