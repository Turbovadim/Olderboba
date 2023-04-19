package net.turbovadim.bisquithost

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.*
import net.turbovadim.bisquithost.DataStore.StoreApiKey
import net.turbovadim.bisquithost.DataStore.getCurrentServerCard
import net.turbovadim.bisquithost.network.RequestsVM
import net.turbovadim.bisquithost.ui.theme.MainNavigation

var apiKey: String? = null

@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            SetStatusbarColor(Color(0xFF0F1418))
            getFuckingKey(context)
            GotoPage(apiKey)

        }
    }
}

fun getFuckingKey(context: Context) = runBlocking {
    val dataStore = StoreApiKey(context)
    apiKey = dataStore.readApiKey()!!
}

@SuppressLint("CoroutineCreationDuringComposition")
@ExperimentalAnimationApi
@Composable
fun GotoPage(apiKey: String?) {
    val requestsVM = viewModel<RequestsVM>()
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    if (context.currentConnectivityState == ConnectionState.Available) {
        if (apiKey.isNullOrBlank()) {
            MainNavigation(ScreensList.StartingPage.route)
        } else {
            scope.launch(Dispatchers.IO) {
                requestsVM.getAccount()
            }
            MainNavigation(ScreensList.ServerListScreen.route)
        }
    } else {
        MainNavigation(ScreensList.BadConnectionScreen.route)
    }
}

@Composable
fun SetStatusbarColor(Color: Color) {
    val view = LocalView.current
    val window = (view.context as Activity).window
    window.statusBarColor = Color.toArgb()
}