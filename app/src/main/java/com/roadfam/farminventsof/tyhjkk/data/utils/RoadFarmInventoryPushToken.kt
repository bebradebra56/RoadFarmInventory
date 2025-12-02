package com.roadfam.farminventsof.tyhjkk.data.utils

import android.util.Log
import com.roadfam.farminventsof.tyhjkk.presentation.app.RoadFarmInventoryApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RoadFarmInventoryPushToken {

    suspend fun roadFarmInventoryGetToken(
        roadFarmInventoryMaxAttempts: Int = 3,
        roadFarmInventoryDelayMs: Long = 1500
    ): String {

        repeat(roadFarmInventoryMaxAttempts - 1) {
            try {
                val roadFarmInventoryToken = FirebaseMessaging.getInstance().token.await()
                return roadFarmInventoryToken
            } catch (e: Exception) {
                Log.e(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(roadFarmInventoryDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}