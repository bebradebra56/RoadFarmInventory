package com.roadfam.farminventsof.data.repository

import com.roadfam.farminventsof.data.dao.*
import com.roadfam.farminventsof.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class InventoryRepository(
    private val categoryDao: CategoryDao,
    private val inventoryItemDao: InventoryItemDao,
    private val reminderDao: ReminderDao,
    private val itemHistoryDao: ItemHistoryDao,
    private val achievementDao: AchievementDao
) {
    // Category operations
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    
    suspend fun getCategoryById(id: Long): Category? = categoryDao.getCategoryById(id)
    
    suspend fun insertCategory(category: Category): Long {
        val id = categoryDao.insertCategory(category)
        checkAchievements()
        return id
    }
    
    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }
    
    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }
    
    // Inventory Item operations
    fun getAllItems(): Flow<List<InventoryItem>> = inventoryItemDao.getAllItems()
    
    suspend fun getItemById(id: Long): InventoryItem? = inventoryItemDao.getItemById(id)
    
    fun getItemsByCategory(categoryId: Long): Flow<List<InventoryItem>> =
        inventoryItemDao.getItemsByCategory(categoryId)
    
    fun getItemsByStatus(status: ItemStatus): Flow<List<InventoryItem>> =
        inventoryItemDao.getItemsByStatus(status)
    
    fun searchItems(query: String): Flow<List<InventoryItem>> =
        inventoryItemDao.searchItems(query)
    
    suspend fun insertItem(item: InventoryItem): Long {
        val id = inventoryItemDao.insertItem(item)
        categoryDao.updateCategoryItemCount(item.categoryId)
        
        // Add history entry
        itemHistoryDao.insertHistory(
            ItemHistory(
                itemId = id,
                action = "Created",
                details = "Item added to inventory"
            )
        )
        
        checkAchievements()
        return id
    }
    
    suspend fun updateItem(item: InventoryItem) {
        val oldItem = inventoryItemDao.getItemById(item.id)
        inventoryItemDao.updateItem(item)
        
        // Update category counts if category changed
        if (oldItem != null && oldItem.categoryId != item.categoryId) {
            categoryDao.updateCategoryItemCount(oldItem.categoryId)
            categoryDao.updateCategoryItemCount(item.categoryId)
        }
        
        // Add history entry if status changed
        if (oldItem != null && oldItem.status != item.status) {
            val action = when (item.status) {
                ItemStatus.WORKING -> "Repaired"
                ItemStatus.NEEDS_REPAIR -> "Needs Repair"
                ItemStatus.BROKEN -> "Broken"
            }
            itemHistoryDao.insertHistory(
                ItemHistory(
                    itemId = item.id,
                    action = action,
                    details = "Status changed"
                )
            )
        }
        
        checkAchievements()
    }
    
    suspend fun deleteItem(item: InventoryItem) {
        inventoryItemDao.deleteItem(item)
        categoryDao.updateCategoryItemCount(item.categoryId)
        itemHistoryDao.deleteHistoryForItem(item.id)
    }
    
    fun getTotalItemCount(): Flow<Int> = inventoryItemDao.getTotalItemCount()
    
    fun getItemCountByStatus(status: ItemStatus): Flow<Int> =
        inventoryItemDao.getItemCountByStatus(status)
    
    // Reminder operations
    fun getAllReminders(): Flow<List<Reminder>> = reminderDao.getAllReminders()
    
    fun getActiveReminders(): Flow<List<Reminder>> = reminderDao.getActiveReminders()
    
    fun getCompletedReminders(): Flow<List<Reminder>> = reminderDao.getCompletedReminders()
    
    suspend fun getReminderById(id: Long): Reminder? = reminderDao.getReminderById(id)
    
    suspend fun insertReminder(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder)
    }
    
    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
        if (reminder.isCompleted) {
            checkAchievements()
        }
    }
    
    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }
    
    fun getCompletedRemindersCount(): Flow<Int> = reminderDao.getCompletedRemindersCount()
    
    // History operations
    fun getHistoryForItem(itemId: Long): Flow<List<ItemHistory>> =
        itemHistoryDao.getHistoryForItem(itemId)
    
    // Achievement operations
    fun getAllAchievements(): Flow<List<Achievement>> = achievementDao.getAllAchievements()
    
    fun getUnlockedAchievements(): Flow<List<Achievement>> = achievementDao.getUnlockedAchievements()
    
    suspend fun unlockAchievement(achievementId: String) {
        val achievement = achievementDao.getAchievementById(achievementId)
        if (achievement != null && !achievement.isUnlocked) {
            achievementDao.updateAchievement(
                achievement.copy(
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis()
                )
            )
        }
    }
    
    private suspend fun checkAchievements() {
        // Check "first_item" achievement
        val totalItems = inventoryItemDao.getTotalItemCount().first()
        if (totalItems >= 1) {
            unlockAchievement("first_item")
        }
        
        // Check "organizer" achievement
        val categories = categoryDao.getAllCategories().first()
        if (categories.size >= 5) {
            unlockAchievement("organizer")
        }
        
        // Check "reminder_master" achievement
        val completedReminders = reminderDao.getCompletedRemindersCount().first()
        if (completedReminders >= 10) {
            unlockAchievement("reminder_master")
        }
        
        // Check "clean_barn" achievement (all items working)
        val needsRepair = inventoryItemDao.getItemCountByStatus(ItemStatus.NEEDS_REPAIR).first()
        val broken = inventoryItemDao.getItemCountByStatus(ItemStatus.BROKEN).first()
        if (totalItems > 0 && needsRepair == 0 && broken == 0) {
            unlockAchievement("clean_barn")
        }
    }
    
    // Clear all data from database
    suspend fun clearAllData() {
        // Get all items and delete them
        val items = getAllItems().first()
        items.forEach { deleteItem(it) }
        
        // Get all reminders and delete them
        val reminders = getAllReminders().first()
        reminders.forEach { deleteReminder(it) }
        
        // Get all categories except defaults and delete them
        val categories = getAllCategories().first()
        categories.forEach { category ->
            if (category.id > 6) { // Don't delete default categories
                deleteCategory(category)
            }
        }
        
        // Reset all achievements
        val achievements = getAllAchievements().first()
        achievements.forEach { achievement ->
            achievementDao.updateAchievement(
                achievement.copy(isUnlocked = false, unlockedAt = null)
            )
        }
    }
}

