package com.roadfam.farminventsof.ui.navigation

sealed class Screen(val route: String) {
    object Inventory : Screen("inventory")
    object AddItem : Screen("add_item")
    object EditItem : Screen("edit_item/{itemId}") {
        fun createRoute(itemId: Long) = "edit_item/$itemId"
    }
    object ItemDetail : Screen("item_detail/{itemId}") {
        fun createRoute(itemId: Long) = "item_detail/$itemId"
    }
    object Categories : Screen("categories")
    object Reminders : Screen("reminders")
    object Analytics : Screen("analytics")
    object Settings : Screen("settings")
}

