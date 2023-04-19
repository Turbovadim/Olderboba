package net.turbovadim.bisquithost.Screens.StartingPage

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun GuideNav(mainNavController: NavController) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = GuideSteps.MainPage.route) {
        composable(GuideSteps.MainPage.route) {
            StartingPage(mainNavController = mainNavController, localNavController = navController)
        }
        composable(GuideSteps.FirstPage.route) {
            FirstPage(navController = navController)
        }
        composable(GuideSteps.SecondPage.route) {
            SecondPage(navController = navController)
        }
        composable(GuideSteps.ThirdPage.route) {
            ThirdPage(navController = navController)
        }
    }
}