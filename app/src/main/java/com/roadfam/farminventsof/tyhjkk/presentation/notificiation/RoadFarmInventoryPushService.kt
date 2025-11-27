package com.roadfam.farminventsof.tyhjkk.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.roadfam.farminventsof.R
import com.roadfam.farminventsof.RoadFarmInventoryActivity
import com.roadfam.farminventsof.tyhjkk.presentation.app.RoadFarmInventoryApplication

private const val ROAD_FARM_INVENTORY_CHANNEL_ID = "road_farm_inventory_notifications"
private const val ROAD_FARM_INVENTORY_CHANNEL_NAME = "RoadFarmInventory Notifications"
private const val ROAD_FARM_INVENTORY_NOT_TAG = "RoadFarmInventory"

class RoadFarmInventoryPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                roadFarmInventoryShowNotification(it.title ?: ROAD_FARM_INVENTORY_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                roadFarmInventoryShowNotification(it.title ?: ROAD_FARM_INVENTORY_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            roadFarmInventoryHandleDataPayload(remoteMessage.data)
        }
    }

    private fun roadFarmInventoryShowNotification(title: String, message: String, data: String?) {
        val roadFarmInventoryNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ROAD_FARM_INVENTORY_CHANNEL_ID,
                ROAD_FARM_INVENTORY_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            roadFarmInventoryNotificationManager.createNotificationChannel(channel)
        }

        val roadFarmInventoryIntent = Intent(this, RoadFarmInventoryActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val roadFarmInventoryPendingIntent = PendingIntent.getActivity(
            this,
            0,
            roadFarmInventoryIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val roadFarmInventoryNotification = NotificationCompat.Builder(this, ROAD_FARM_INVENTORY_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.road_farm_inventory_noti)
            .setAutoCancel(true)
            .setContentIntent(roadFarmInventoryPendingIntent)
            .build()

        roadFarmInventoryNotificationManager.notify(System.currentTimeMillis().toInt(), roadFarmInventoryNotification)
    }

    private fun roadFarmInventoryHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}