package com.roadfam.farminventsof.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

object AchievementsList {
    fun getAll() = listOf(
        Achievement(
            "good_owner",
            "Good Owner",
            "7 days without broken items",
            "🏆"
        ),
        Achievement(
            "clean_barn",
            "Clean Barn",
            "All items checked",
            "✨"
        ),
        Achievement(
            "first_item",
            "First Steps",
            "Add your first item",
            "🌟"
        ),
        Achievement(
            "organizer",
            "Organizer",
            "Create 5 categories",
            "📋"
        ),
        Achievement(
            "reminder_master",
            "Never Forget",
            "Complete 10 reminders",
            "⏰"
        )
    )
}

