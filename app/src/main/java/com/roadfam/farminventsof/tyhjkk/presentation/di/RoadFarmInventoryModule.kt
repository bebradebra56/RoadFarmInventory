package com.roadfam.farminventsof.tyhjkk.presentation.di

import com.roadfam.farminventsof.tyhjkk.data.repo.RoadFarmInventoryRepository
import com.roadfam.farminventsof.tyhjkk.data.shar.RoadFarmInventorySharedPreference
import com.roadfam.farminventsof.tyhjkk.data.utils.RoadFarmInventoryPushToken
import com.roadfam.farminventsof.tyhjkk.data.utils.RoadFarmInventorySystemService
import com.roadfam.farminventsof.tyhjkk.domain.usecases.RoadFarmInventoryGetAllUseCase
import com.roadfam.farminventsof.tyhjkk.presentation.pushhandler.RoadFarmInventoryPushHandler
import com.roadfam.farminventsof.tyhjkk.presentation.ui.load.RoadFarmInventoryLoadViewModel
import com.roadfam.farminventsof.tyhjkk.presentation.ui.view.RoadFarmInventoryViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val roadFarmInventoryModule = module {
    factory {
        RoadFarmInventoryPushHandler()
    }
    single {
        RoadFarmInventoryRepository()
    }
    single {
        RoadFarmInventorySharedPreference(get())
    }
    factory {
        RoadFarmInventoryPushToken()
    }
    factory {
        RoadFarmInventorySystemService(get())
    }
    factory {
        RoadFarmInventoryGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        RoadFarmInventoryViFun(get())
    }
    viewModel {
        RoadFarmInventoryLoadViewModel(get(), get(), get())
    }
}