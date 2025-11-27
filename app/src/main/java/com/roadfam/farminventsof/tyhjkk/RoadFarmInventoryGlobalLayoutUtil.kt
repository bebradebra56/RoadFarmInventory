package com.roadfam.farminventsof.tyhjkk

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.roadfam.farminventsof.tyhjkk.presentation.app.RoadFarmInventoryApplication

class RoadFarmInventoryGlobalLayoutUtil {

    private var roadFarmInventoryMChildOfContent: View? = null
    private var roadFarmInventoryUsableHeightPrevious = 0

    fun roadFarmInventoryAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        roadFarmInventoryMChildOfContent = content.getChildAt(0)

        roadFarmInventoryMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val roadFarmInventoryUsableHeightNow = roadFarmInventoryComputeUsableHeight()
        if (roadFarmInventoryUsableHeightNow != roadFarmInventoryUsableHeightPrevious) {
            val roadFarmInventoryUsableHeightSansKeyboard = roadFarmInventoryMChildOfContent?.rootView?.height ?: 0
            val roadFarmInventoryHeightDifference = roadFarmInventoryUsableHeightSansKeyboard - roadFarmInventoryUsableHeightNow

            if (roadFarmInventoryHeightDifference > (roadFarmInventoryUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(RoadFarmInventoryApplication.roadFarmInventoryInputMode)
            } else {
                activity.window.setSoftInputMode(RoadFarmInventoryApplication.roadFarmInventoryInputMode)
            }
//            mChildOfContent?.requestLayout()
            roadFarmInventoryUsableHeightPrevious = roadFarmInventoryUsableHeightNow
        }
    }

    private fun roadFarmInventoryComputeUsableHeight(): Int {
        val r = Rect()
        roadFarmInventoryMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}