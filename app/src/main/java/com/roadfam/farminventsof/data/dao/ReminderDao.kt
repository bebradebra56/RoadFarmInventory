package com.roadfam.farminventsof.data.dao

import androidx.room.*
import com.roadfam.farminventsof.data.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY date ASC")
    fun getAllReminders(): Flow<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE isCompleted = 0 ORDER BY date ASC")
    fun getActiveReminders(): Flow<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE isCompleted = 1 ORDER BY date DESC")
    fun getCompletedReminders(): Flow<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): Reminder?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long
    
    @Update
    suspend fun updateReminder(reminder: Reminder)
    
    @Delete
    suspend fun deleteReminder(reminder: Reminder)
    
    @Query("SELECT COUNT(*) FROM reminders WHERE isCompleted = 1")
    fun getCompletedRemindersCount(): Flow<Int>
}

