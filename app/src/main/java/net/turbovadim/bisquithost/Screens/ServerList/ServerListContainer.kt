package net.turbovadim.bisquithost.Screens.ServerList

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PanoramaWideAngle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.turbovadim.bisquithost.*
import net.turbovadim.bisquithost.Components.NarrowServerCard
import net.turbovadim.bisquithost.DataStore.StoreCurrentCard
import net.turbovadim.bisquithost.DataStore.getCurrentServerCard
import net.turbovadim.bisquithost.R
import net.turbovadim.bisquithost.ServerCards.WideServerCard
import net.turbovadim.bisquithost.network.RequestsVM
import net.turbovadim.bisquithost.ui.theme.AppBarColor
import net.turbovadim.bisquithost.ui.theme.SemiWhite200
import kotlin.math.pow
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ServerListScreen(navController: NavController) {
    val requestsVM = viewModel<RequestsVM>()
    val serverListVM = viewModel<ServerListVM>()
    val scope = rememberCoroutineScope()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                requestsVM.setIsLoaded(false)
                CoroutineScope(Dispatchers.IO).launch {
                    requestsVM.listServers()
                }
            }
    }
        lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
        lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val context = LocalContext.current
    serverListVM.changeCurrentCard(getCurrentServerCard(context)!!)

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        TopAppBar(
            modifier = Modifier
                .fillMaxWidth(),
            navigationIcon = {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "sdf",
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .height(40.dp),
                    )
            },
            title = {
                Text(
                    text = "Bisquit.Host",
                    color = SemiWhite200,
                    fontWeight = FontWeight.W500,
                    fontFamily = FontFamily.SansSerif
                )
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = AppBarColor),

            actions = {
                Row {
                    val rotation = remember { mutableStateOf(if (serverListVM.currentCard.value == "narrow") {90f} else {0f}) }
                    IconButton(
                        onClick = {
                            if (serverListVM.currentCard.value == "narrow") {
                                serverListVM.changeCurrentCard("wide")
                                scope.launch {
                                    val dataStore = StoreCurrentCard(context)
                                    dataStore.saveCurrentCard("wide")
                                    rotation.value = 0f
                                }
                            } else {
                                serverListVM.changeCurrentCard("narrow")
                                scope.launch {
                                    val dataStore = StoreCurrentCard(context)
                                    dataStore.saveCurrentCard("narrow")
                                    rotation.value = 90f
                                }
                            }
                        }
                    ) {

                        val currentRotation = animateFloatAsState(targetValue = rotation.value)
                        Icon(
                            Icons.Rounded.PanoramaWideAngle,
                            modifier = Modifier.rotate(currentRotation.value),
                            tint = SemiWhite200,
                            contentDescription = "Localized description"
                        )
                    }
                    Box(
                        Modifier
                            .wrapContentSize(Alignment.CenterEnd)
                    ) {
                        IconButton(onClick = {
                            requestsVM.setExpanded(true)
                        }) {
                            Icon(
                                Icons.Rounded.MoreVert,
                                tint = SemiWhite200,
                                contentDescription = "Localized description"
                            )
                            DropdownMenu(
                                expanded = requestsVM.expanded.value,
                                onDismissRequest = { requestsVM.setExpanded(false) },
                            ) {
                                DropdownMenuItem(
                                    modifier = Modifier
                                        .defaultMinSize(minWidth = 0.dp, minHeight = 0.dp)
                                        .height(30.dp),
                                    text = {Text("Сменить API ключ")},
                                    onClick = {
                                        requestsVM.setExpanded(false)
                                        navController.navigate(ScreensList.StartingPage.route)
                                    })
                            }
                        }
                    }
                }
            }
        )
        ServerListContainer(navController)
    }
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ServerListContainer(navController: NavController) {
    val requestsVM = viewModel<RequestsVM>()
    Crossfade(
        animationSpec = tween(durationMillis = 800),
        targetState = requestsVM.isLoaded.value, label = ""
    ) { isLoaded ->
        if (isLoaded) {
            ServerCards(navController)
        } else {
            ServersLoadingScreen()
        }
    }
}

@Composable
fun ServerCards(navController: NavController) {
    val serverListVM = viewModel<ServerListVM>()
    if (serverListVM.currentCard.value == "narrow") {
        NarrowServerCard(navController)
    } else {
        WideServerCard(navController)
    }
}

@Composable
fun ServersLoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        CircularProgressIndicator(color = Color.White )
    }
}

fun Double.roundTo(numFractionDigits: Int): Double {
    val factor = 10.0.pow(numFractionDigits.toDouble())
    return (this * factor).roundToInt() / factor
}

fun serverStateColor(serverState: String): Color {
    return when (serverState) {
        "running" -> (Color(0xFF1AB641))
        "offline" -> (Color(0xFFBE1717))
        "starting" -> (Color(0xFFC29421))
        "stopping" -> (Color(0xFFC29421))
        else -> (Color.White)
    }
}
