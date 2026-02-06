package com.roadfam.farminventsof.tyhjkk.data.shar

import android.content.Context
import androidx.core.content.edit

class RoadFarmInventorySharedPreference(context: Context) {
    private val roadFarmInventoryPrefs = context.getSharedPreferences("roadFarmInventorySharedPrefsAb", Context.MODE_PRIVATE)

    var roadFarmInventorySavedUrl: String
        get() = roadFarmInventoryPrefs.getString(ROAD_FARM_INVENTORY_SAVED_URL, "") ?: ""
        set(value) = roadFarmInventoryPrefs.edit { putString(ROAD_FARM_INVENTORY_SAVED_URL, value) }

    var roadFarmInventoryExpired : Long
        get() = roadFarmInventoryPrefs.getLong(ROAD_FARM_INVENTORY_EXPIRED, 0L)
        set(value) = roadFarmInventoryPrefs.edit { putLong(ROAD_FARM_INVENTORY_EXPIRED, value) }

    var roadFarmInventoryAppState: Int
        get() = roadFarmInventoryPrefs.getInt(ROAD_FARM_INVENTORY_APPLICATION_STATE, 0)
        set(value) = roadFarmInventoryPrefs.edit { putInt(ROAD_FARM_INVENTORY_APPLICATION_STATE, value) }

    var roadFarmInventoryNotificationRequest: Long
        get() = roadFarmInventoryPrefs.getLong(ROAD_FARM_INVENTORY_NOTIFICAITON_REQUEST, 0L)
        set(value) = roadFarmInventoryPrefs.edit { putLong(ROAD_FARM_INVENTORY_NOTIFICAITON_REQUEST, value) }


    var roadFarmInventoryNotificationState:Int
        get() = roadFarmInventoryPrefs.getInt(ICE_MIND_NOTIFICATION_STATE, 0)
        set(value) = roadFarmInventoryPrefs.edit { putInt(ICE_MIND_NOTIFICATION_STATE, value) }

    companion object {
        private const val ICE_MIND_NOTIFICATION_STATE = "roadFarmInventoryNotificationState"
        private const val ROAD_FARM_INVENTORY_SAVED_URL = "roadFarmInventorySavedUrl"
        private const val ROAD_FARM_INVENTORY_EXPIRED = "roadFarmInventoryExpired"
        private const val ROAD_FARM_INVENTORY_APPLICATION_STATE = "roadFarmInventoryApplicationState"
        private const val ROAD_FARM_INVENTORY_NOTIFICAITON_REQUEST = "roadFarmInventoryNotificationRequest"
    }
}