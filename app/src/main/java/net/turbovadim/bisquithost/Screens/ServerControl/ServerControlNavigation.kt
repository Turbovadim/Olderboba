package net.turbovadim.bisquithost.Screens.ServerControl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.turbovadim.bisquithost.Screens.ServerControl.Backups.BackupsContainer
import net.turbovadim.bisquithost.Screens.ServerControl.FileManager.FileManagerNavigation

@Composable
fun ServerControlNav(
    navController: NavHostController,
    viewModelStoreOwner: ViewModelStoreOwner
) {
    NavHost(navController = navController, startDestination = ServerControlPagesList.ConsolePage.route) {
        composable(
            ServerControlPagesList.ConsolePage.route,

        ) {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner
            ) {
                ConsolePage()
            }
        }
        composable(ServerControlPagesList.SettingsPage.route) {
            SettingsContainer()
        }
        composable(ServerControlPagesList.BackupsPage.route) {
            BackupsContainer()
        }
        composable(ServerControlPagesList.FilesPage.route) {
            FileManagerNavigation()
        }
    }
}