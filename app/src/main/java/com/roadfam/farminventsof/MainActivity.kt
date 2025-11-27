package com.roadfam.farminventsof

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.roadfam.farminventsof.ui.navigation.NavigationGraph
import com.roadfam.farminventsof.ui.theme.RoadFarmInventoryTheme
import com.roadfam.farminventsof.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    
    private lateinit var viewModelFactory: ViewModelFactory
    
    private lateinit var inventoryViewModel: InventoryViewModel
    private lateinit var addEditItemViewModel: AddEditItemViewModel
    private lateinit var itemDetailViewModel: ItemDetailViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var reminderViewModel: ReminderViewModel
    private lateinit var analyticsViewModel: AnalyticsViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize ViewModels
        viewModelFactory = ViewModelFactory(applicationContext)
        
        inventoryViewModel = ViewModelProvider(this, viewModelFactory)[InventoryViewModel::class.java]
        addEditItemViewModel = ViewModelProvider(this, viewModelFactory)[AddEditItemViewModel::class.java]
        itemDetailViewModel = ViewModelProvider(this, viewModelFactory)[ItemDetailViewModel::class.java]
        categoryViewModel = ViewModelProvider(this, viewModelFactory)[CategoryViewModel::class.java]
        reminderViewModel = ViewModelProvider(this, viewModelFactory)[ReminderViewModel::class.java]
        analyticsViewModel = ViewModelProvider(this, viewModelFactory)[AnalyticsViewModel::class.java]
        settingsViewModel = ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]
        
        setContent {
            FarmInventoryApp()
        }
    }
    
    @Composable
    private fun FarmInventoryApp() {
        val themeMode by settingsViewModel.themeMode.collectAsState()
        val navController = rememberNavController()
        
        val darkTheme = themeMode == "dark"
        
        RoadFarmInventoryTheme(darkTheme = darkTheme) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                NavigationGraph(
                    navController = navController,
                    inventoryViewModel = inventoryViewModel,
                    addEditItemViewModel = addEditItemViewModel,
                    itemDetailViewModel = itemDetailViewModel,
                    categoryViewModel = categoryViewModel,
                    reminderViewModel = reminderViewModel,
                    analyticsViewModel = analyticsViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
