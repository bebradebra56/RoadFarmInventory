package com.roadfam.farminventsof.tyhjkk.presentation.ui.view

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RoadFarmInventoryViFun(private val context: Context) {
    fun roadFarmInventorySavePhoto() : Uri {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        val df = sdf.format(Date())
        val dir = context.filesDir.absoluteFile
        if (!dir.exists()) {
            dir.mkdir()
        }
        return FileProvider.getUriForFile(
            context,
            "com.roadfam.farminventsofffffff",
            File(dir, "/$df.jpg")
        )
    }

}