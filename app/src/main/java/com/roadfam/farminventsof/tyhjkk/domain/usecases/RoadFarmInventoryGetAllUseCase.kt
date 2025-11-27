package com.roadfam.farminventsof.tyhjkk.domain.usecases

import android.util.Log
import com.roadfam.farminventsof.tyhjkk.data.repo.RoadFarmInventoryRepository
import com.roadfam.farminventsof.tyhjkk.data.utils.RoadFarmInventoryPushToken
import com.roadfam.farminventsof.tyhjkk.data.utils.RoadFarmInventorySystemService
import com.roadfam.farminventsof.tyhjkk.domain.model.RoadFarmInventoryEntity
import com.roadfam.farminventsof.tyhjkk.domain.model.RoadFarmInventoryParam
import com.roadfam.farminventsof.tyhjkk.presentation.app.RoadFarmInventoryApplication

class RoadFarmInventoryGetAllUseCase(
    private val roadFarmInventoryRepository: RoadFarmInventoryRepository,
    private val roadFarmInventorySystemService: RoadFarmInventorySystemService,
    private val roadFarmInventoryPushToken: RoadFarmInventoryPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : RoadFarmInventoryEntity?{
        val params = RoadFarmInventoryParam(
            roadFarmInventoryLocale = roadFarmInventorySystemService.roadFarmInventoryGetLocale(),
            roadFarmInventoryPushToken = roadFarmInventoryPushToken.roadFarmInventoryGetToken(),
            roadFarmInventoryAfId = roadFarmInventorySystemService.roadFarmInventoryGetAppsflyerId()
        )
        Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "Params for request: $params")
        return roadFarmInventoryRepository.roadFarmInventoryGetClient(params, conversion)
    }



}