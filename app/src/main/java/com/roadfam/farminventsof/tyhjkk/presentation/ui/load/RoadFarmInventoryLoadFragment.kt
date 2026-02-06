package com.roadfam.farminventsof.tyhjkk.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.roadfam.farminventsof.MainActivity
import com.roadfam.farminventsof.R
import com.roadfam.farminventsof.databinding.FragmentLoadRoadFarmInventoryBinding
import com.roadfam.farminventsof.tyhjkk.data.shar.RoadFarmInventorySharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class RoadFarmInventoryLoadFragment : Fragment(R.layout.fragment_load_road_farm_inventory) {
    private lateinit var roadFarmInventoryLoadBinding: FragmentLoadRoadFarmInventoryBinding

    private val roadFarmInventoryLoadViewModel by viewModel<RoadFarmInventoryLoadViewModel>()

    private val roadFarmInventorySharedPreference by inject<RoadFarmInventorySharedPreference>()

    private var roadFarmInventoryUrl = ""

    private val roadFarmInventoryRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        roadFarmInventorySharedPreference.roadFarmInventoryNotificationState = 2
        roadFarmInventoryNavigateToSuccess(roadFarmInventoryUrl)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        roadFarmInventoryLoadBinding = FragmentLoadRoadFarmInventoryBinding.bind(view)

        roadFarmInventoryLoadBinding.roadFarmInventoryGrandButton.setOnClickListener {
            val roadFarmInventoryPermission = Manifest.permission.POST_NOTIFICATIONS
            roadFarmInventoryRequestNotificationPermission.launch(roadFarmInventoryPermission)
        }

        roadFarmInventoryLoadBinding.roadFarmInventorySkipButton.setOnClickListener {
            roadFarmInventorySharedPreference.roadFarmInventoryNotificationState = 1
            roadFarmInventorySharedPreference.roadFarmInventoryNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            roadFarmInventoryNavigateToSuccess(roadFarmInventoryUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                roadFarmInventoryLoadViewModel.roadFarmInventoryHomeScreenState.collect {
                    when (it) {
                        is RoadFarmInventoryLoadViewModel.RoadFarmInventoryHomeScreenState.RoadFarmInventoryLoading -> {

                        }

                        is RoadFarmInventoryLoadViewModel.RoadFarmInventoryHomeScreenState.RoadFarmInventoryError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is RoadFarmInventoryLoadViewModel.RoadFarmInventoryHomeScreenState.RoadFarmInventorySuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val roadFarmInventoryNotificationState = roadFarmInventorySharedPreference.roadFarmInventoryNotificationState
                                when (roadFarmInventoryNotificationState) {
                                    0 -> {
                                        roadFarmInventoryLoadBinding.roadFarmInventoryNotiGroup.visibility = View.VISIBLE
                                        roadFarmInventoryLoadBinding.roadFarmInventoryLoadingGroup.visibility = View.GONE
                                        roadFarmInventoryUrl = it.data
                                    }
                                    1 -> {
                                        if (System.currentTimeMillis() / 1000 > roadFarmInventorySharedPreference.roadFarmInventoryNotificationRequest) {
                                            roadFarmInventoryLoadBinding.roadFarmInventoryNotiGroup.visibility = View.VISIBLE
                                            roadFarmInventoryLoadBinding.roadFarmInventoryLoadingGroup.visibility = View.GONE
                                            roadFarmInventoryUrl = it.data
                                        } else {
                                            roadFarmInventoryNavigateToSuccess(it.data)
                                        }
                                    }
                                    2 -> {
                                        roadFarmInventoryNavigateToSuccess(it.data)
                                    }
                                }
                            } else {
                                roadFarmInventoryNavigateToSuccess(it.data)
                            }
                        }

                        RoadFarmInventoryLoadViewModel.RoadFarmInventoryHomeScreenState.RoadFarmInventoryNotInternet -> {
                            roadFarmInventoryLoadBinding.roadFarmInventoryStateGroup.visibility = View.VISIBLE
                            roadFarmInventoryLoadBinding.roadFarmInventoryLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun roadFarmInventoryNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_roadFarmInventoryLoadFragment_to_roadFarmInventoryV,
            bundleOf(ROAD_FARM_INVENTORY_D to data)
        )
    }

    companion object {
        const val ROAD_FARM_INVENTORY_D = "roadFarmInventoryData"
    }
}