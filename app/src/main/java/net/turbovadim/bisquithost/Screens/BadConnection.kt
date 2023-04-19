package net.turbovadim.bisquithost.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.turbovadim.bisquithost.*

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BadConnection(navController: NavController) {
    val scope = rememberCoroutineScope()
    val connection by connectivityState()
    val isConnected = connection === ConnectionState.Available
    Column(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(bottom = 25.dp),
            text = "Отсутствует подключение к интернету \nОжидаем восстановления соединения",
            color = Color(0xFFC21D1D),
            fontWeight = FontWeight.W500,
            fontSize = 16.sp
        )
        CircularProgressIndicator(color = Color.White )
        if (isConnected) {
            if (apiKey.isNullOrBlank()) {
                scope.launch {
                    delay(1000)
                    navController.navigate(ScreensList.StartingPage.route)
                }

            } else {
                scope.launch {
                    delay(1000)
                    navController.navigate(ScreensList.ServerListScreen.route)
                }

            }
        }
    }
}