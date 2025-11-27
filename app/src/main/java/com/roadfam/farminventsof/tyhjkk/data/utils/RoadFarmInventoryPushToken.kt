package com.roadfam.farminventsof.tyhjkk.data.utils

import android.util.Log
import com.roadfam.farminventsof.tyhjkk.presentation.app.RoadFarmInventoryApplication
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RoadFarmInventoryPushToken {

    suspend fun roadFarmInventoryGetToken(): String = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    continuation.resume(it.result)
                    Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "Token error: ${it.exception}")
                } else {
                    continuation.resume(it.result)
                }
            }
        } catch (e: Exception) {
            Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "FirebaseMessagingPushToken = null")
            continuation.resume("")
        }
    }


}