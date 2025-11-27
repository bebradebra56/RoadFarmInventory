package com.roadfam.farminventsof.tyhjkk.data.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.appsflyer.AppsFlyerLib
import com.roadfam.farminventsof.tyhjkk.presentation.app.RoadFarmInventoryApplication
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class RoadFarmInventorySystemService(private val context: Context) {

    suspend fun roadFarmInventoryGetGaid() : String  = withContext(Dispatchers.IO){
        val gaid = AdvertisingIdClient.getAdvertisingIdInfo(context).id ?: "00000000-0000-0000-0000-000000000000"
        Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "Gaid: $gaid")
        return@withContext gaid
    }

    fun roadFarmInventoryGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(context) ?: ""
        Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    fun roadFarmInventoryGetLocale() : String {
        return  Locale.getDefault().language
    }

    fun roadFarmInventoryIsOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                return true
            }
        }
        return false
    }

}