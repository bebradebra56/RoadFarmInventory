package com.roadfam.farminventsof.tyhjkk.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roadfam.farminventsof.tyhjkk.data.shar.RoadFarmInventorySharedPreference
import com.roadfam.farminventsof.tyhjkk.data.utils.RoadFarmInventorySystemService
import com.roadfam.farminventsof.tyhjkk.domain.usecases.RoadFarmInventoryGetAllUseCase
import com.roadfam.farminventsof.tyhjkk.presentation.app.RoadFarmInventoryAppsFlyerState
import com.roadfam.farminventsof.tyhjkk.presentation.app.RoadFarmInventoryApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoadFarmInventoryLoadViewModel(
    private val roadFarmInventoryGetAllUseCase: RoadFarmInventoryGetAllUseCase,
    private val roadFarmInventorySharedPreference: RoadFarmInventorySharedPreference,
    private val roadFarmInventorySystemService: RoadFarmInventorySystemService
) : ViewModel() {

    private val _roadFarmInventoryHomeScreenState: MutableStateFlow<RoadFarmInventoryHomeScreenState> =
        MutableStateFlow(RoadFarmInventoryHomeScreenState.RoadFarmInventoryLoading)
    val roadFarmInventoryHomeScreenState = _roadFarmInventoryHomeScreenState.asStateFlow()

    private var roadFarmInventoryGetApps = false


    init {
        viewModelScope.launch {
            when (roadFarmInventorySharedPreference.roadFarmInventoryAppState) {
                0 -> {
                    if (roadFarmInventorySystemService.roadFarmInventoryIsOnline()) {
                        RoadFarmInventoryApplication.roadFarmInventoryConversionFlow.collect {
                            when(it) {
                                RoadFarmInventoryAppsFlyerState.RoadFarmInventoryDefault -> {}
                                RoadFarmInventoryAppsFlyerState.RoadFarmInventoryError -> {
                                    roadFarmInventorySharedPreference.roadFarmInventoryAppState = 2
                                    _roadFarmInventoryHomeScreenState.value =
                                        RoadFarmInventoryHomeScreenState.RoadFarmInventoryError
                                    roadFarmInventoryGetApps = true
                                }
                                is RoadFarmInventoryAppsFlyerState.RoadFarmInventorySuccess -> {
                                    if (!roadFarmInventoryGetApps) {
                                        roadFarmInventoryGetData(it.roadFarmInventoryData)
                                        roadFarmInventoryGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _roadFarmInventoryHomeScreenState.value =
                            RoadFarmInventoryHomeScreenState.RoadFarmInventoryNotInternet
                    }
                }
                1 -> {
                    if (roadFarmInventorySystemService.roadFarmInventoryIsOnline()) {
                        if (RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_FB_LI != null) {
                            _roadFarmInventoryHomeScreenState.value =
                                RoadFarmInventoryHomeScreenState.RoadFarmInventorySuccess(
                                    RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > roadFarmInventorySharedPreference.roadFarmInventoryExpired) {
                            Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "Current time more then expired, repeat request")
                            RoadFarmInventoryApplication.roadFarmInventoryConversionFlow.collect {
                                when(it) {
                                    RoadFarmInventoryAppsFlyerState.RoadFarmInventoryDefault -> {}
                                    RoadFarmInventoryAppsFlyerState.RoadFarmInventoryError -> {
                                        _roadFarmInventoryHomeScreenState.value =
                                            RoadFarmInventoryHomeScreenState.RoadFarmInventorySuccess(
                                                roadFarmInventorySharedPreference.roadFarmInventorySavedUrl
                                            )
                                        roadFarmInventoryGetApps = true
                                    }
                                    is RoadFarmInventoryAppsFlyerState.RoadFarmInventorySuccess -> {
                                        if (!roadFarmInventoryGetApps) {
                                            roadFarmInventoryGetData(it.roadFarmInventoryData)
                                            roadFarmInventoryGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "Current time less then expired, use saved url")
                            _roadFarmInventoryHomeScreenState.value =
                                RoadFarmInventoryHomeScreenState.RoadFarmInventorySuccess(
                                    roadFarmInventorySharedPreference.roadFarmInventorySavedUrl
                                )
                        }
                    } else {
                        _roadFarmInventoryHomeScreenState.value =
                            RoadFarmInventoryHomeScreenState.RoadFarmInventoryNotInternet
                    }
                }
                2 -> {
                    _roadFarmInventoryHomeScreenState.value =
                        RoadFarmInventoryHomeScreenState.RoadFarmInventoryError
                }
            }
        }
    }


    private suspend fun roadFarmInventoryGetData(conversation: MutableMap<String, Any>?) {
        val roadFarmInventoryData = roadFarmInventoryGetAllUseCase.invoke(conversation)
        if (roadFarmInventorySharedPreference.roadFarmInventoryAppState == 0) {
            if (roadFarmInventoryData == null) {
                roadFarmInventorySharedPreference.roadFarmInventoryAppState = 2
                _roadFarmInventoryHomeScreenState.value =
                    RoadFarmInventoryHomeScreenState.RoadFarmInventoryError
            } else {
                roadFarmInventorySharedPreference.roadFarmInventoryAppState = 1
                roadFarmInventorySharedPreference.apply {
                    roadFarmInventoryExpired = roadFarmInventoryData.roadFarmInventoryExpires
                    roadFarmInventorySavedUrl = roadFarmInventoryData.roadFarmInventoryUrl
                }
                _roadFarmInventoryHomeScreenState.value =
                    RoadFarmInventoryHomeScreenState.RoadFarmInventorySuccess(roadFarmInventoryData.roadFarmInventoryUrl)
            }
        } else  {
            if (roadFarmInventoryData == null) {
                _roadFarmInventoryHomeScreenState.value =
                    RoadFarmInventoryHomeScreenState.RoadFarmInventorySuccess(roadFarmInventorySharedPreference.roadFarmInventorySavedUrl)
            } else {
                roadFarmInventorySharedPreference.apply {
                    roadFarmInventoryExpired = roadFarmInventoryData.roadFarmInventoryExpires
                    roadFarmInventorySavedUrl = roadFarmInventoryData.roadFarmInventoryUrl
                }
                _roadFarmInventoryHomeScreenState.value =
                    RoadFarmInventoryHomeScreenState.RoadFarmInventorySuccess(roadFarmInventoryData.roadFarmInventoryUrl)
            }
        }
    }


    sealed class RoadFarmInventoryHomeScreenState {
        data object RoadFarmInventoryLoading : RoadFarmInventoryHomeScreenState()
        data object RoadFarmInventoryError : RoadFarmInventoryHomeScreenState()
        data class RoadFarmInventorySuccess(val data: String) : RoadFarmInventoryHomeScreenState()
        data object RoadFarmInventoryNotInternet: RoadFarmInventoryHomeScreenState()
    }
}