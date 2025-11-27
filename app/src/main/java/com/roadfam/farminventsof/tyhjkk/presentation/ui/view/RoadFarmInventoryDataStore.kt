package com.roadfam.farminventsof.tyhjkk.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class RoadFarmInventoryDataStore : ViewModel(){
    val roadFarmInventoryViList: MutableList<RoadFarmInventoryVi> = mutableListOf()
    var roadFarmInventoryIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var roadFarmInventoryContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var roadFarmInventoryView: RoadFarmInventoryVi

}