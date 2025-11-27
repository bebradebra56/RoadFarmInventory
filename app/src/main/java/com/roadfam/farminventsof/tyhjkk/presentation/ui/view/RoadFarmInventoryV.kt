package com.roadfam.farminventsof.tyhjkk.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.roadfam.farminventsof.tyhjkk.presentation.app.RoadFarmInventoryApplication
import com.roadfam.farminventsof.tyhjkk.presentation.ui.load.RoadFarmInventoryLoadFragment
import org.koin.android.ext.android.inject

class RoadFarmInventoryV : Fragment(){

    private lateinit var roadFarmInventoryPhoto: Uri
    private var roadFarmInventoryFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val roadFarmInventoryTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        roadFarmInventoryFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        roadFarmInventoryFilePathFromChrome = null
    }

    private val roadFarmInventoryTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            roadFarmInventoryFilePathFromChrome?.onReceiveValue(arrayOf(roadFarmInventoryPhoto))
            roadFarmInventoryFilePathFromChrome = null
        } else {
            roadFarmInventoryFilePathFromChrome?.onReceiveValue(null)
            roadFarmInventoryFilePathFromChrome = null
        }
    }

    private val roadFarmInventoryDataStore by activityViewModels<RoadFarmInventoryDataStore>()


    private val roadFarmInventoryViFun by inject<RoadFarmInventoryViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (roadFarmInventoryDataStore.roadFarmInventoryView.canGoBack()) {
                        roadFarmInventoryDataStore.roadFarmInventoryView.goBack()
                        Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "WebView can go back")
                    } else if (roadFarmInventoryDataStore.roadFarmInventoryViList.size > 1) {
                        Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "WebView can`t go back")
                        roadFarmInventoryDataStore.roadFarmInventoryViList.removeAt(roadFarmInventoryDataStore.roadFarmInventoryViList.lastIndex)
                        Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "WebView list size ${roadFarmInventoryDataStore.roadFarmInventoryViList.size}")
                        roadFarmInventoryDataStore.roadFarmInventoryView.destroy()
                        val previousWebView = roadFarmInventoryDataStore.roadFarmInventoryViList.last()
                        roadFarmInventoryAttachWebViewToContainer(previousWebView)
                        roadFarmInventoryDataStore.roadFarmInventoryView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (roadFarmInventoryDataStore.roadFarmInventoryIsFirstCreate) {
            roadFarmInventoryDataStore.roadFarmInventoryIsFirstCreate = false
            roadFarmInventoryDataStore.roadFarmInventoryContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return roadFarmInventoryDataStore.roadFarmInventoryContainerView
        } else {
            return roadFarmInventoryDataStore.roadFarmInventoryContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "onViewCreated")
        if (roadFarmInventoryDataStore.roadFarmInventoryViList.isEmpty()) {
            roadFarmInventoryDataStore.roadFarmInventoryView = RoadFarmInventoryVi(requireContext(), object :
                RoadFarmInventoryCallBack {
                override fun roadFarmInventoryHandleCreateWebWindowRequest(roadFarmInventoryVi: RoadFarmInventoryVi) {
                    roadFarmInventoryDataStore.roadFarmInventoryViList.add(roadFarmInventoryVi)
                    Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "WebView list size = ${roadFarmInventoryDataStore.roadFarmInventoryViList.size}")
                    Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "CreateWebWindowRequest")
                    roadFarmInventoryDataStore.roadFarmInventoryView = roadFarmInventoryVi
                    roadFarmInventoryVi.roadFarmInventorySetFileChooserHandler { callback ->
                        roadFarmInventoryHandleFileChooser(callback)
                    }
                    roadFarmInventoryAttachWebViewToContainer(roadFarmInventoryVi)
                }

            }, roadFarmInventoryWindow = requireActivity().window).apply {
                roadFarmInventorySetFileChooserHandler { callback ->
                    roadFarmInventoryHandleFileChooser(callback)
                }
            }
            roadFarmInventoryDataStore.roadFarmInventoryView.roadFarmInventoryFLoad(arguments?.getString(
                RoadFarmInventoryLoadFragment.ROAD_FARM_INVENTORY_D) ?: "")
//            ejvview.fLoad("www.google.com")
            roadFarmInventoryDataStore.roadFarmInventoryViList.add(roadFarmInventoryDataStore.roadFarmInventoryView)
            roadFarmInventoryAttachWebViewToContainer(roadFarmInventoryDataStore.roadFarmInventoryView)
        } else {
            roadFarmInventoryDataStore.roadFarmInventoryViList.forEach { webView ->
                webView.roadFarmInventorySetFileChooserHandler { callback ->
                    roadFarmInventoryHandleFileChooser(callback)
                }
            }
            roadFarmInventoryDataStore.roadFarmInventoryView = roadFarmInventoryDataStore.roadFarmInventoryViList.last()

            roadFarmInventoryAttachWebViewToContainer(roadFarmInventoryDataStore.roadFarmInventoryView)
        }
        Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "WebView list size = ${roadFarmInventoryDataStore.roadFarmInventoryViList.size}")
    }

    private fun roadFarmInventoryHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        roadFarmInventoryFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "Launching file picker")
                    roadFarmInventoryTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "Launching camera")
                    roadFarmInventoryPhoto = roadFarmInventoryViFun.roadFarmInventorySavePhoto()
                    roadFarmInventoryTakePhoto.launch(roadFarmInventoryPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(RoadFarmInventoryApplication.ROAD_FARM_INVENTORY_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                roadFarmInventoryFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun roadFarmInventoryAttachWebViewToContainer(w: RoadFarmInventoryVi) {
        roadFarmInventoryDataStore.roadFarmInventoryContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            roadFarmInventoryDataStore.roadFarmInventoryContainerView.removeAllViews()
            roadFarmInventoryDataStore.roadFarmInventoryContainerView.addView(w)
        }
    }


}