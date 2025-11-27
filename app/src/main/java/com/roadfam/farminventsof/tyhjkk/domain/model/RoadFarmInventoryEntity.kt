package com.roadfam.farminventsof.tyhjkk.domain.model

import com.google.gson.annotations.SerializedName


data class RoadFarmInventoryEntity (
    @SerializedName("ok")
    val roadFarmInventoryOk: String,
    @SerializedName("url")
    val roadFarmInventoryUrl: String,
    @SerializedName("expires")
    val roadFarmInventoryExpires: Long,
)