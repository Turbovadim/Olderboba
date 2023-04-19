package net.turbovadim.bisquithost.Screens.ServerControl

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.turbovadim.bisquithost.*
import net.turbovadim.bisquithost.Handlers.BackHandler
import net.turbovadim.bisquithost.network.InitWebsocket
import net.turbovadim.bisquithost.network.RequestsVM
import net.turbovadim.bisquithost.network.WebsocketViewModel
import net.turbovadim.bisquithost.ui.theme.CascadiaMono
import net.turbovadim.bisquithost.ui.theme.Gray200
import net.turbovadim.bisquithost.ui.theme.SemiWhite200
import net.turbovadim.bisquithost.Screens.ServerList.serverStateColor

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ServerControl(
    navController: NavController,
    serverId: String,
    name: String,
    ramLimit: Int,
    diskLimit: Int,
    cpuLimit: Int,
    sftpUrl: String,
    backupsLimit: Int,
) {

    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }

    val websocketVM: WebsocketViewModel = viewModel()

    val navController2 = rememberNavController()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        websocketVM.generateWebsocketKey(InitWebsocket.INIT)
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    websocketVM.closeWebsocket()
                    websocketVM.consoleMessages.clear()
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            viewModelStoreOwner.viewModelStore.clear()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BackHandler(enabled = true, onBack = {
        navController.navigate(ScreensList.ServerListScreen.route)
    })

    ServerId = serverId
    Name = name
    RamLimit = ramLimit
    CpuLimit = cpuLimit
    DiskLimit = diskLimit
    SftpUrl = sftpUrl
    BackupsLimit = backupsLimit

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigate(ScreensList.ServerListScreen.route) }
                    ) {
                        Icon(Icons.Rounded.Home, "backIcon", tint = SemiWhite200)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = "Bisquit.Host",
                        color = SemiWhite200,
                        fontWeight = FontWeight.W500,
                        fontFamily = FontFamily.SansSerif
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF192027))
            )
        },
        bottomBar = {
            BottomNavBar(navController2)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF000000))
                .padding(paddingValues),
            horizontalAlignment = Alignment.End
        ) {
            ServerControlNav(
                navController = navController2,
                viewModelStoreOwner = viewModelStoreOwner
            )
        }
    }
}


@Composable
fun ConsolePage() {
    val requestsVM: RequestsVM = viewModel()
    val websocketVM: WebsocketViewModel = viewModel()
    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollState()
    val texts = remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(0.1f)
            ) {}
            Row(
                modifier = Modifier
                    .weight(0.8f)
                    .padding(top = 10.dp, bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W500,
                    text = Name,
                    color = Color.White,
                    modifier = Modifier
                        .padding(end = 10.dp)
                )
                val serverStateColor = animateColorAsState(
                    animationSpec = tween(durationMillis = 800),
                    targetValue = serverStateColor(websocketVM.serverState.value), label = ""
                )
                Canvas(
                    modifier = Modifier
                        .width(6.5.dp)
                        .height(6.5.dp)
                        .offset(y = 1.5.dp)
                ) {
                    drawCircle(
                        color = serverStateColor.value,
                        radius = size.minDimension
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .weight(0.1f),
            ) {
                Checkbox(
                    modifier = Modifier
                        .height(20.dp)
                        .width(20.dp)
                        .offset(y = 3.dp, x = (-5).dp),
                    checked = websocketVM.autoScroll.value,
                    onCheckedChange = {
                        websocketVM.setAutoScroll(it)
                    },
                    colors = CheckboxDefaults.colors(uncheckedColor = Gray200)
                )
            }
        }

        ConsoleContainer()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 5.dp, start = 5.dp, end = 5.dp)
                .fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.891f)
                    .padding(end = 5.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(color = Gray200),
            ) {
                BasicTextField(
                    keyboardOptions = KeyboardOptions(autoCorrect = false),
                    keyboardActions = KeyboardActions(onDone = {
                        scope.launch(Dispatchers.IO) {
                            requestsVM.sendCommand(ServerId, texts.value)
                            texts.value = ""
                        }
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    value = texts.value,
                    singleLine = true,
                    textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
                    onValueChange = { texts.value = it },
                    decorationBox = { innerTextField ->
                        Row {
                            if (texts.value.isBlank()) {
                                Text(
                                    text = "Введите команду",
                                    color = Color.Black,
                                    fontSize = 15.sp
                                )
                            }
                        }
                        innerTextField()
                    },
                )
            }
            Button(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        requestsVM.sendCommand(ServerId, texts.value)
                        texts.value = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minWidth = 40.dp, minHeight = 40.dp)
                    .width(40.dp)
                    .height(40.dp),
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(0.0001.dp),
            ) {
                Icon(
                    Icons.Rounded.Send,
                    contentDescription = "иконка",
                    modifier = Modifier.size(23.dp)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, start = 5.dp, end = 5.dp, bottom = 5.dp)
        ) {
            val buttonsName = listOf("Старт", "Рестарт", "Стоп", "Убить")
            val buttonActions = listOf("start", "restart", "stop", "kill")
            val buttonColors =
                listOf(Color(0xFF32C739), Color(0xFFFF9800), Color(0xFFD61717), Color(0xFF555555))

            buttonActions.forEachIndexed { index, action ->
                Button(
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColors[index]
                    ),
                    shape = MaterialTheme.shapes.medium,
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            requestsVM.changePowerState(ServerId, action)
                        }
                    }
                ) { Text(text = buttonsName[index], color = Color.Black) }
            }
        }
        StatCards()
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ConsoleContainer() {
    val websocketVM = viewModel<WebsocketViewModel>()
    Box(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .height(400.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(Gray200)
    ) {
        Box(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF4B4B4B))
        ) {
            LazyColumn(
                state = websocketVM.listState,
                modifier = Modifier.padding(horizontal = 5.dp),
                reverseLayout = false,
            ) {
                itemsIndexed(websocketVM.consoleMessages) { _, message ->
                    Text(
                        modifier = Modifier,
                        text = message,
                        color = Color.White,
                        fontFamily = CascadiaMono,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun StatCards() {
    val websocketVM = viewModel<WebsocketViewModel>()

    Row(
        modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Card(
            modifier = Modifier
                .weight(1f),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = Gray200)
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 10.dp, top = 5.dp, bottom = 5.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    fontWeight = FontWeight(490),
                    text = "Нагрузка на Процессор"
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (websocketVM.serverState.value != "offline") {
                        Text(
                            modifier = Modifier.padding(end = 2.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight(600),
                            text = websocketVM.statsArray.value[0]
                        )
                        Text(
                            fontWeight = FontWeight(500),
                            modifier = Modifier.offset(y = (0.9).dp),
                            text = "/ $CpuLimit%"
                        )
                    } else {
                        Text(
                            modifier = Modifier.padding(end = 2.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight(600),
                            text = "Выключен"
                        )
                    }

                }
            }
        }
        Card(
            modifier = Modifier
                .weight(1f),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = Gray200)
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 10.dp, top = 5.dp, bottom = 5.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    fontWeight = FontWeight(460),
                    text = "Память (ОЗУ)"
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (websocketVM.serverState.value != "offline") {
                        Text(
                            modifier = Modifier.padding(end = 2.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight(600),
                            text = websocketVM.statsArray.value[1]
                        )
                        Text(
                            fontWeight = FontWeight(500),
                            modifier = Modifier.offset(y = (0.9).dp),
                            text = "/ " + RamLimit / 1024 + "GB"
                        )
                    } else {
                        Text(
                            modifier = Modifier.padding(end = 2.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight(600),
                            text = "Выключен"
                        )
                    }

                }

            }
        }
    }
    Row(
        modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Card(
            modifier = Modifier
                .weight(1f),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = Gray200)
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 10.dp, top = 5.dp, bottom = 5.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    fontWeight = FontWeight(490),
                    text = "SSD Диск"
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(end = 2.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight(600),
                        text = websocketVM.statsArray.value[2]
                    )
                    Text(
                        fontWeight = FontWeight(500),
                        modifier = Modifier.offset(y = (0.9).dp),
                        text = "/ " + DiskLimit / 1024 + "GB"
                    )
                }
            }
        }
        Card(
            modifier = Modifier
                .weight(1f),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = Gray200)
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 10.dp, top = 5.dp, bottom = 5.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    fontWeight = FontWeight(460),
                    text = "Время работы"
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (websocketVM.serverState.value != "offline") {
                        Text(
                            modifier = Modifier.padding(end = 2.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight(600),
                            text = websocketVM.statsArray.value[3]
                        )
                    } else {
                        Text(
                            modifier = Modifier.padding(end = 2.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight(600),
                            text = "Выключен"
                        )
                    }

                }

            }
        }
    }
}