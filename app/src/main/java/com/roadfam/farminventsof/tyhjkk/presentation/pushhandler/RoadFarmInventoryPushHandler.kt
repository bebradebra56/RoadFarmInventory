package com.roadfam.farminventsof.tyhjkk.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.roadfam.farminventsof.tyhjkk.presentation.app.RoadFarmInventoryApplication

class RoadFarmInventoryPushHandler {
    fun roadFarmInventoryHandlePush(extras: Bundle?) {
        Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = roadFarmInventoryBundleToMap(extras)
            Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_FB_LI = map["url"]
                    Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "Push data no!")
        }
    }

    private fun roadFarmInventoryBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}