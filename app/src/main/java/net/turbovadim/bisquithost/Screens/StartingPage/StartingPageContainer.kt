package net.turbovadim.bisquithost.Screens.StartingPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import net.turbovadim.bisquithost.DataStore.StoreApiKey
import net.turbovadim.bisquithost.Handlers.BackHandler
import net.turbovadim.bisquithost.R
import net.turbovadim.bisquithost.ScreensList
import net.turbovadim.bisquithost.apiKey
import net.turbovadim.bisquithost.ui.theme.Gray200
import net.turbovadim.bisquithost.ui.theme.Gray300
import net.turbovadim.bisquithost.ui.theme.Gray400
import net.turbovadim.bisquithost.ui.theme.LightBlue300

@Composable
fun StartingPageContainer(navController: NavController) {
    GuideNav(mainNavController = navController)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StartingPage(mainNavController: NavController, localNavController: NavController) {
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val dataStore = StoreApiKey(context)

    val texts = remember { mutableStateOf("") }
    BackHandler(enabled = true, onBack = {
        mainNavController.navigate(ScreensList.ServerListScreen.route)
    })

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .clip(MaterialTheme.shapes.large)
                .background(Gray300)
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
            ) {
                Text(
                    fontWeight = FontWeight.W500,
                    text = "Bisquit.Host",
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 35.sp,
                    modifier = Modifier
                        .background(Gray400)
                        .padding(vertical = 7.dp)
                        .fillMaxWidth()
                )
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "dfgdfg",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(vertical = 10.dp)
                )
                Box (
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(color = Gray200),
                ) {
                    BasicTextField(
                        keyboardOptions = KeyboardOptions(autoCorrect = false),
                        keyboardActions = KeyboardActions(onDone = {}),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        value = texts.value,
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
                        onValueChange = {texts.value = it},
                        decorationBox = { innerTextField ->
                            Row() {
                                if (texts.value.isBlank()) {
                                    Text(
                                        text = "Введите API-Ключ",
                                        color = Color.Black,
                                        fontSize = 15.sp
                                    )
                                } else {}
                            }
                            innerTextField()
                        },
                    )
                }
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .height(40.dp)
                            .weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E94CA)),
                        onClick = {
                              localNavController.navigate(GuideSteps.FirstPage.route)
                        },
                    ) {
                        Text(text = "Где найти API-Ключ?")
                    }
                    Button(
                        modifier = Modifier
                            .height(40.dp)
                            .weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = LightBlue300),
                        onClick = {
                            if (!texts.value.isBlank()) {
                                apiKey = texts.value
                                keyboardController?.hide()
                                scope.launch {
                                    dataStore.saveApiKey(texts.value)
                                    println("MyLog: saving apikey")
                                    mainNavController.navigate(ScreensList.ServerListScreen.route)
                                }
                            } else if (!apiKey.isNullOrBlank()) {
                                mainNavController.navigate(ScreensList.ServerListScreen.route)
                            }

                        },
                    ) {
                        Text(text = "Сохранить")
                    }
                }
            }
        }
    }
}