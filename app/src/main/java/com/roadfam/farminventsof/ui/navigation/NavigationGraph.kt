package com.roadfam.farminventsof.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.roadfam.farminventsof.ui.screens.*
import com.roadfam.farminventsof.ui.viewmodel.*

@Composable
fun NavigationGraph(
    navController: NavHostController,
    inventoryViewModel: InventoryViewModel,
    addEditItemViewModel: AddEditItemViewModel,
    itemDetailViewModel: ItemDetailViewModel,
    categoryViewModel: CategoryViewModel,
    reminderViewModel: ReminderViewModel,
    analyticsViewModel: AnalyticsViewModel,
    settingsViewModel: SettingsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Inventory.route
    ) {
        composable(Screen.Inventory.route) {
            InventoryScreen(
                viewModel = inventoryViewModel,
                onAddItem = { navController.navigate(Screen.AddItem.route) },
                onItemClick = { itemId ->
                    navController.navigate(Screen.ItemDetail.createRoute(itemId))
                },
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                onNavigateToReminders = { navController.navigate(Screen.Reminders.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        composable(Screen.AddItem.route) {
            AddEditItemScreen(
                viewModel = addEditItemViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.EditItem.route,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId") ?: return@composable
            AddEditItemScreen(
                viewModel = addEditItemViewModel,
                itemId = itemId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.ItemDetail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId") ?: return@composable
            ItemDetailScreen(
                viewModel = itemDetailViewModel,
                itemId = itemId,
                onNavigateBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.EditItem.createRoute(itemId)) }
            )
        }
        
        composable(Screen.Categories.route) {
            CategoriesScreen(
                viewModel = categoryViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Reminders.route) {
            RemindersScreen(
                viewModel = reminderViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToInventory = { navController.navigate(Screen.Inventory.route) },
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        composable(Screen.Analytics.route) {
            AnalyticsScreen(
                viewModel = analyticsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToInventory = { navController.navigate(Screen.Inventory.route) },
                onNavigateToReminders = { navController.navigate(Screen.Reminders.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToInventory = { navController.navigate(Screen.Inventory.route) },
                onNavigateToReminders = { navController.navigate(Screen.Reminders.route) },
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) }
            )
        }
    }
}

