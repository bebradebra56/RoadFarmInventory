package com.roadfam.farminventsof.data.database

import androidx.room.TypeConverter
import com.roadfam.farminventsof.data.model.ItemStatus
import com.roadfam.farminventsof.data.model.ReminderFrequency

class Converters {
    @TypeConverter
    fun fromItemStatus(value: ItemStatus): String {
        return value.name
    }
    
    @TypeConverter
    fun toItemStatus(value: String): ItemStatus {
        return try {
            ItemStatus.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ItemStatus.WORKING
        }
    }
    
    @TypeConverter
    fun fromReminderFrequency(value: ReminderFrequency): String {
        return value.name
    }
    
    @TypeConverter
    fun toReminderFrequency(value: String): ReminderFrequency {
        return try {
            ReminderFrequency.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ReminderFrequency.ONCE
        }
    }
}

