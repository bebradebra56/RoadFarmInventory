package com.roadfam.farminventsof.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    
    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val CURRENCY = stringPreferencesKey("currency")
        val EASTER_EGG_FOUND = booleanPreferencesKey("easter_egg_found")
        val GOOD_OWNER_START_DATE = longPreferencesKey("good_owner_start_date")
    }
    
    val themeMode: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.THEME_MODE] ?: "light"
        }
    
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
        }
    
    val currency: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.CURRENCY] ?: "USD"
        }
    
    val easterEggFound: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.EASTER_EGG_FOUND] ?: false
        }
    
    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENCY] = currency
        }
    }
    
    suspend fun setEasterEggFound(found: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EASTER_EGG_FOUND] = found
        }
    }
    
    suspend fun startGoodOwnerTracking() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GOOD_OWNER_START_DATE] = System.currentTimeMillis()
        }
    }
    
    suspend fun resetAllData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    suspend fun clearAppData(context: android.content.Context) {
        // Clear DataStore
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

