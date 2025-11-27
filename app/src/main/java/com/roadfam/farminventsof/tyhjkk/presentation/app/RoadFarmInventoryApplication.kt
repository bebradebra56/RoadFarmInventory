package com.roadfam.farminventsof.tyhjkk.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.roadfam.farminventsof.tyhjkk.presentation.di.roadFarmInventoryModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface RoadFarmInventoryAppsFlyerState {
    data object RoadFarmInventoryDefault : RoadFarmInventoryAppsFlyerState
    data class RoadFarmInventorySuccess(val roadFarmInventoryData: MutableMap<String, Any>?) :
        RoadFarmInventoryAppsFlyerState

    data object RoadFarmInventoryError : RoadFarmInventoryAppsFlyerState
}

interface RoadFarmInventoryAppsApi {
    @Headers("Content-Type: application/json")
    @GET(ROAD_FARM_INVENTORY_LIN)
    fun roadFarmInventoryGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val ROAD_FARM_INVENTORY_APP_DEV = "zJjGm2o6TX7GVWyuJXkueR"
private const val ROAD_FARM_INVENTORY_LIN = "com.roadfam.farminventsof"

class RoadFarmInventoryApplication : Application() {
    private var roadFarmInventoryIsResumed = false
    private var roadFarmInventoryConversionTimeoutJob: Job? = null
    private var roadFarmInventoryDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        roadFarmInventorySetDebufLogger(appsflyer)
        roadFarmInventoryMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        roadFarmInventoryExtractDeepMap(p0.deepLink)
                        Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            ROAD_FARM_INVENTORY_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    roadFarmInventoryConversionTimeoutJob?.cancel()
                    Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = roadFarmInventoryGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.roadFarmInventoryGetClient(
                                    devkey = ROAD_FARM_INVENTORY_APP_DEV,
                                    deviceId = roadFarmInventoryGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    roadFarmInventoryResume(RoadFarmInventoryAppsFlyerState.RoadFarmInventoryError)
                                } else {
                                    roadFarmInventoryResume(
                                        RoadFarmInventoryAppsFlyerState.RoadFarmInventorySuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "Error: ${d.message}")
                                roadFarmInventoryResume(RoadFarmInventoryAppsFlyerState.RoadFarmInventoryError)
                            }
                        }
                    } else {
                        roadFarmInventoryResume(RoadFarmInventoryAppsFlyerState.RoadFarmInventorySuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    roadFarmInventoryConversionTimeoutJob?.cancel()
                    Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "onConversionDataFail: $p0")
                    roadFarmInventoryResume(RoadFarmInventoryAppsFlyerState.RoadFarmInventoryError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, ROAD_FARM_INVENTORY_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        roadFarmInventoryStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@RoadFarmInventoryApplication)
            modules(
                listOf(
                    roadFarmInventoryModule
                )
            )
        }
    }

    private fun roadFarmInventoryExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "Extracted DeepLink data: $map")
        roadFarmInventoryDeepLinkData = map
    }

    private fun roadFarmInventoryStartConversionTimeout() {
        roadFarmInventoryConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!roadFarmInventoryIsResumed) {
                Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                roadFarmInventoryResume(RoadFarmInventoryAppsFlyerState.RoadFarmInventoryError)
            }
        }
    }

    private fun roadFarmInventoryResume(state: RoadFarmInventoryAppsFlyerState) {
        roadFarmInventoryConversionTimeoutJob?.cancel()
        if (state is RoadFarmInventoryAppsFlyerState.RoadFarmInventorySuccess) {
            val convData = state.roadFarmInventoryData ?: mutableMapOf()
            val deepData = roadFarmInventoryDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!roadFarmInventoryIsResumed) {
                roadFarmInventoryIsResumed = true
                roadFarmInventoryConversionFlow.value = RoadFarmInventoryAppsFlyerState.RoadFarmInventorySuccess(merged)
            }
        } else {
            if (!roadFarmInventoryIsResumed) {
                roadFarmInventoryIsResumed = true
                roadFarmInventoryConversionFlow.value = state
            }
        }
    }

    private fun roadFarmInventoryGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun roadFarmInventorySetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun roadFarmInventoryMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun roadFarmInventoryGetApi(url: String, client: OkHttpClient?): RoadFarmInventoryAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {
        var roadFarmInventoryInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val roadFarmInventoryConversionFlow: MutableStateFlow<RoadFarmInventoryAppsFlyerState> = MutableStateFlow(
            RoadFarmInventoryAppsFlyerState.RoadFarmInventoryDefault
        )
        var ROAD_FARM_INVENTORY_FB_LI: String? = null
        const val ROAD_FARM_INVENTORY_MAIN_TAG = "RoadFarmInventoryMainTag"
    }
}