package net.turbovadim.bisquithost.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import net.turbovadim.bisquithost.Screens.BadConnection
import net.turbovadim.bisquithost.Screens.ServerList.ServerListScreen
import net.turbovadim.bisquithost.ScreensList
import net.turbovadim.bisquithost.Screens.ServerControl.ServerControl
import net.turbovadim.bisquithost.Screens.StartingPage.StartingPageContainer

@ExperimentalAnimationApi
@Composable
fun MainNavigation(firstScreen: String) {
    val navController = rememberAnimatedNavController()
    val uri = "https://mgr.bisquit.host"

    AnimatedNavHost(
        navController = navController,
        startDestination = firstScreen,
    ) {
        navigation(startDestination = "sdfsdf", route = "test") {

        }
        composable(
            route = ScreensList.BadConnectionScreen.route,
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(500))
            },
        ) {
            BadConnection(navController = navController)
        }

        composable(
            route = ScreensList.StartingPage.route,
            enterTransition = {
                if (initialState.destination.route == ScreensList.BadConnectionScreen.route) {
                    slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(500))
                } else {
                    fadeIn()
                }
            },
            exitTransition = {
                fadeOut()
            },
        ) {
            StartingPageContainer(navController)
        }

        composable(
            route = ScreensList.ServerListScreen.route,
            enterTransition = {
                when (initialState.destination.route) {
                    ScreensList.StartingPage.route -> {
                        fadeIn()
                    }
                    ScreensList.BadConnectionScreen.route -> {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(500))
                    }
                    else -> {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(500))
                    }
                }
            },
            exitTransition = {
                if (targetState.destination.route  == ScreensList.StartingPage.route ) {
                    fadeOut()
                } else {slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(500))}
                },
        ) {
            ServerListScreen(navController)
        }

        composable(
            route = ScreensList.ServerControlScreen.route + "/{serverId}/{name}/{ramLimit}/{cpuLimit}/{diskLimit}/{sftpUrl}/{backupsLimit}",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(500))
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Right,
                    animationSpec = tween(500))
            },
            arguments = listOf(
                navArgument("serverId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("ramLimit") {
                    type = NavType.IntType
                    defaultValue = 0
                    nullable = false
                },
                navArgument("cpuLimit") {
                    type = NavType.IntType
                    defaultValue = 0
                    nullable = false
                },
                navArgument("diskLimit") {
                    type = NavType.IntType
                    defaultValue = 0
                    nullable = false
                },
                navArgument("sftpUrl") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                },
                navArgument("backupsLimit") {
                    type = NavType.IntType
                    defaultValue = 0
                    nullable = false
                }
            )
        ) { entry ->
            ServerControl(
                navController = navController,
                serverId = entry.arguments?.getString("serverId")!!,
                name = entry.arguments?.getString("name")!!,
                ramLimit = entry.arguments?.getInt("ramLimit")!!,
                cpuLimit = entry.arguments?.getInt("cpuLimit")!!,
                diskLimit = entry.arguments?.getInt("diskLimit")!!,
                sftpUrl = entry.arguments?.getString("sftpUrl")!!,
                backupsLimit = entry.arguments?.getInt("backupsLimit")!!,
            )
        }

    }
}