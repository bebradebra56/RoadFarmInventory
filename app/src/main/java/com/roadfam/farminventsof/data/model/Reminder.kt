package com.roadfam.farminventsof.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: Long,
    val frequency: ReminderFrequency = ReminderFrequency.ONCE,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ReminderFrequency {
    ONCE,
    DAILY,
    WEEKLY,
    MONTHLY
}

