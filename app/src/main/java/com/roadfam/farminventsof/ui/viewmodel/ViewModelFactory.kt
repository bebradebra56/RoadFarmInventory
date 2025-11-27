package com.roadfam.farminventsof.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.roadfam.farminventsof.data.database.AppDatabase
import com.roadfam.farminventsof.data.preferences.PreferencesManager
import com.roadfam.farminventsof.data.repository.InventoryRepository

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    
    private val database by lazy { AppDatabase.getDatabase(context) }
    private val repository by lazy {
        InventoryRepository(
            categoryDao = database.categoryDao(),
            inventoryItemDao = database.inventoryItemDao(),
            reminderDao = database.reminderDao(),
            itemHistoryDao = database.itemHistoryDao(),
            achievementDao = database.achievementDao()
        )
    }
    private val preferencesManager by lazy { PreferencesManager(context) }
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(InventoryViewModel::class.java) -> {
                InventoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddEditItemViewModel::class.java) -> {
                AddEditItemViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ItemDetailViewModel::class.java) -> {
                ItemDetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> {
                CategoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ReminderViewModel::class.java) -> {
                ReminderViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AnalyticsViewModel::class.java) -> {
                AnalyticsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(preferencesManager, repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

