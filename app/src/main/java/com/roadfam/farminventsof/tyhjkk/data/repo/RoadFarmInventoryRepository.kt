package com.roadfam.farminventsof.tyhjkk.data.repo

import android.util.Log
import com.roadfam.farminventsof.tyhjkk.domain.model.RoadFarmInventoryEntity
import com.roadfam.farminventsof.tyhjkk.domain.model.RoadFarmInventoryParam
import com.roadfam.farminventsof.tyhjkk.presentation.app.RoadFarmInventoryApplication.Companion.ROAD_FARM_INVENTORY_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RoadFarmInventoryApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun roadFarmInventoryGetClient(
        @Body jsonString: JsonObject,
    ): Call<RoadFarmInventoryEntity>
}


private const val ROAD_FARM_INVENTORY_MAIN = "https://roadfarminventtory.com/"
class RoadFarmInventoryRepository {

    suspend fun roadFarmInventoryGetClient(
        roadFarmInventoryParam: RoadFarmInventoryParam,
        roadFarmInventoryConversion: MutableMap<String, Any>?
    ): RoadFarmInventoryEntity? {
        val gson = Gson()
        val api = roadFarmInventoryGetApi(ROAD_FARM_INVENTORY_MAIN, null)

        val roadFarmInventoryJsonObject = gson.toJsonTree(roadFarmInventoryParam).asJsonObject
        roadFarmInventoryConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            roadFarmInventoryJsonObject.add(key, element)
        }
        return try {
            val roadFarmInventoryRequest: Call<RoadFarmInventoryEntity> = api.roadFarmInventoryGetClient(
                jsonString = roadFarmInventoryJsonObject,
            )
            val roadFarmInventoryResult = roadFarmInventoryRequest.awaitResponse()
            Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "Retrofit: Result code: ${roadFarmInventoryResult.code()}")
            if (roadFarmInventoryResult.code() == 200) {
                Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "Retrofit: Get request success")
                Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "Retrofit: Code = ${roadFarmInventoryResult.code()}")
                Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "Retrofit: ${roadFarmInventoryResult.body()}")
                roadFarmInventoryResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(ROAD_FARM_INVENTORY_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun roadFarmInventoryGetApi(url: String, client: OkHttpClient?) : RoadFarmInventoryApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
