package com.roadfam.farminventsof.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val categoryId: Long,
    val quantity: Int,
    val status: ItemStatus,
    val lastUsedDate: Long = System.currentTimeMillis(),
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class ItemStatus {
    WORKING,      // Green - Everything is fine
    NEEDS_REPAIR, // Orange - Needs attention
    BROKEN        // Red - Out of order/depleted
}

