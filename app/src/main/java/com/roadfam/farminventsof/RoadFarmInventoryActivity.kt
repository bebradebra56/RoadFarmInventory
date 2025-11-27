package com.roadfam.farminventsof

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.roadfam.farminventsof.tyhjkk.RoadFarmInventoryGlobalLayoutUtil
import com.roadfam.farminventsof.tyhjkk.presentation.app.RoadFarmInventoryApplication
import com.roadfam.farminventsof.tyhjkk.presentation.pushhandler.RoadFarmInventoryPushHandler
import com.roadfam.farminventsof.tyhjkk.roadFarmInventorySetupSystemBars
import org.koin.android.ext.android.inject

class RoadFarmInventoryActivity : AppCompatActivity() {

    private val roadFarmInventoryPushHandler by inject<RoadFarmInventoryPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        roadFarmInventorySetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_road_farm_inventory)

        val roadFarmInventoryRootView = findViewById<View>(android.R.id.content)
        RoadFarmInventoryGlobalLayoutUtil().roadFarmInventoryAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(roadFarmInventoryRootView) { roadFarmInventoryView, roadFarmInventoryInsets ->
            val roadFarmInventorySystemBars = roadFarmInventoryInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val roadFarmInventoryDisplayCutout = roadFarmInventoryInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val roadFarmInventoryIme = roadFarmInventoryInsets.getInsets(WindowInsetsCompat.Type.ime())


            val roadFarmInventoryTopPadding = maxOf(roadFarmInventorySystemBars.top, roadFarmInventoryDisplayCutout.top)
            val roadFarmInventoryLeftPadding = maxOf(roadFarmInventorySystemBars.left, roadFarmInventoryDisplayCutout.left)
            val roadFarmInventoryRightPadding = maxOf(roadFarmInventorySystemBars.right, roadFarmInventoryDisplayCutout.right)
            window.setSoftInputMode(RoadFarmInventoryApplication.roadFarmInventoryInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "ADJUST PUN")
                val roadFarmInventoryBottomInset = maxOf(roadFarmInventorySystemBars.bottom, roadFarmInventoryDisplayCutout.bottom)

                roadFarmInventoryView.setPadding(roadFarmInventoryLeftPadding, roadFarmInventoryTopPadding, roadFarmInventoryRightPadding, 0)

                roadFarmInventoryView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = roadFarmInventoryBottomInset
                }
            } else {
                Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "ADJUST RESIZE")

                val roadFarmInventoryBottomInset = maxOf(roadFarmInventorySystemBars.bottom, roadFarmInventoryDisplayCutout.bottom, roadFarmInventoryIme.bottom)

                roadFarmInventoryView.setPadding(roadFarmInventoryLeftPadding, roadFarmInventoryTopPadding, roadFarmInventoryRightPadding, 0)

                roadFarmInventoryView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = roadFarmInventoryBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "Activity onCreate()")
        roadFarmInventoryPushHandler.roadFarmInventoryHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            roadFarmInventorySetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        roadFarmInventorySetupSystemBars()
    }
}