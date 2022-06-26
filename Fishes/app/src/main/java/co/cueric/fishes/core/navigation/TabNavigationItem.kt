package co.cueric.fishes.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Icons list at https://fonts.google.com/icons
 */
sealed class TabNavigationItem(var title: String, var icon: ImageVector, var screen_route: String) {
    object Home : TabNavigationItem("Home", Icons.Outlined.Home, "home")
    object Cart : TabNavigationItem("Cart", Icons.Outlined.ShoppingBag, "cart")
    object Add : TabNavigationItem("Add", Icons.Outlined.Add, "add")
    object Notification : TabNavigationItem("Noti", Icons.Outlined.Notifications, "notification")
    object Profile : TabNavigationItem("Profile", Icons.Outlined.ManageAccounts, "profile")
}