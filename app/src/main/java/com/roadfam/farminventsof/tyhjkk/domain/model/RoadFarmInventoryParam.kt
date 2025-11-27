package com.roadfam.farminventsof.tyhjkk.domain.model

import com.google.gson.annotations.SerializedName


private const val ROAD_FARM_INVENTORY_A = "com.roadfam.farminventsof"
private const val ROAD_FARM_INVENTORY_B = "roadfarminventory"
data class RoadFarmInventoryParam (
    @SerializedName("af_id")
    val roadFarmInventoryAfId: String,
    @SerializedName("bundle_id")
    val roadFarmInventoryBundleId: String = ROAD_FARM_INVENTORY_A,
    @SerializedName("os")
    val roadFarmInventoryOs: String = "Android",
    @SerializedName("store_id")
    val roadFarmInventoryStoreId: String = ROAD_FARM_INVENTORY_A,
    @SerializedName("locale")
    val roadFarmInventoryLocale: String,
    @SerializedName("push_token")
    val roadFarmInventoryPushToken: String,
    @SerializedName("firebase_project_id")
    val roadFarmInventoryFirebaseProjectId: String = ROAD_FARM_INVENTORY_B,

    )