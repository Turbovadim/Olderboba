package net.turbovadim.bisquithost.Screens.ServerControl.FileManager

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun FileManagerNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = FileManagerScreens.FileManager.route + "?directory={directory}") {
        composable(
            route = FileManagerScreens.FileManager.route + "?directory={directory}",
            arguments = listOf(
                navArgument("directory") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { entry ->
            FilesContainer(
                navController = navController,
                directory = entry.arguments?.getString("directory")!!
            )
        }
        composable(
            route = FileManagerScreens.EditFile.route + "?file={file}&directory={directory}",
            arguments = listOf(
                navArgument("directory") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("file") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { entry ->
            EditFile(
                navController = navController,
                directory = entry.arguments?.getString("directory")!!,
                file = entry.arguments?.getString("file")!!,
            )
        }
        composable(
            route = FileManagerScreens.ViewImage.route + "?file={file}&directory={directory}",
            arguments = listOf(
                navArgument("directory") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("file") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { entry ->
            ViewImage(
                navController = navController,
                directory = entry.arguments?.getString("directory")!!,
                file = entry.arguments?.getString("file")!!,
            )
        }
    }
}