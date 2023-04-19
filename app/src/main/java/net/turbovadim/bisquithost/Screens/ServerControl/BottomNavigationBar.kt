package net.turbovadim.bisquithost.Screens.ServerControl

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val selected_icon: ImageVector,
    val route: String,
)

object NavBarItems {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Консоль",
            icon = Icons.Outlined.Terminal,
            selected_icon = Icons.Rounded.Terminal,
            route = ServerControlPagesList.ConsolePage.route
        ),
        BottomNavItem(
            label = "Файлы",
            icon = Icons.Outlined.Folder,
            selected_icon = Icons.Rounded.Folder,
            route = ServerControlPagesList.FilesPage.route
        ),
        BottomNavItem(
            label = "Запуск",
            icon = Icons.Outlined.CloudUpload,
            selected_icon = Icons.Rounded.CloudUpload,
            route = ServerControlPagesList.BackupsPage.route
        ),
        BottomNavItem(
            label = "Настройки",
            icon = Icons.Outlined.Settings,
            selected_icon = Icons.Rounded.Settings,
            route = ServerControlPagesList.SettingsPage.route
        ),
    )
}

@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar(
        containerColor = Color(0xFF192027),
        modifier = Modifier.height(55.dp),
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavBarItems.BottomNavItems.forEach { navItem ->
            val isSelected = currentRoute == navItem.route
            val icon = if (isSelected) {navItem.selected_icon} else {navItem.icon}
            NavigationBarItem(
                modifier = Modifier
                    .height(55.dp)
                ,
                selected = isSelected,
                onClick = {
                    if (currentRoute != navItem.route) {
                            navController.navigate(navItem.route)
                        }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = navItem.label,
                        tint = Color(0xFFAEB9C4)
                    )
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFF323F4D))
            )
        }
    }
}