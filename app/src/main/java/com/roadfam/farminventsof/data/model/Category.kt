package com.roadfam.farminventsof.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: String,
    val itemCount: Int = 0
)

// Predefined categories
object DefaultCategories {
    fun getAll() = listOf(
        Category(1, "Tools", "🪣", "#9E9E9E"),
        Category(2, "Feed", "🌾", "#66BB6A"),
        Category(3, "Electrical", "💡", "#F4B400"),
        Category(4, "Machinery", "🚜", "#F28C38"),
        Category(5, "Supplies", "📦", "#3E7BB6"),
        Category(6, "Other", "📝", "#6B6B6B")
    )
}

