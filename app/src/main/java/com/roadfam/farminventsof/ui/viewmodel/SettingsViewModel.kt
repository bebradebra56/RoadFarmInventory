package com.roadfam.farminventsof.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roadfam.farminventsof.data.model.Achievement
import com.roadfam.farminventsof.data.preferences.PreferencesManager
import com.roadfam.farminventsof.data.repository.InventoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val repository: InventoryRepository
) : ViewModel() {
    
    val themeMode: StateFlow<String> = preferencesManager.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "farm"
        )
    
    val notificationsEnabled: StateFlow<Boolean> = preferencesManager.notificationsEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
    
    val currency: StateFlow<String> = preferencesManager.currency
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "USD"
        )
    
    val easterEggFound: StateFlow<Boolean> = preferencesManager.easterEggFound
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    val achievements: StateFlow<List<Achievement>> = repository.getAllAchievements()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
        }
    }
    
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
        }
    }
    
    fun setCurrency(currency: String) {
        viewModelScope.launch {
            preferencesManager.setCurrency(currency)
        }
    }
    
    fun unlockEasterEgg() {
        viewModelScope.launch {
            preferencesManager.setEasterEggFound(true)
        }
    }
    
    fun resetAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                // Clear all database data
                repository.clearAllData()
                
                // Clear all preferences
                preferencesManager.resetAllData()
                
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

