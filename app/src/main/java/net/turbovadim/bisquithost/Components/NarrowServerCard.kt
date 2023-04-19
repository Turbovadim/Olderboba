package net.turbovadim.bisquithost.Components

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.turbovadim.bisquithost.*
import net.turbovadim.bisquithost.Screens.ServerList.roundTo
import net.turbovadim.bisquithost.Screens.ServerList.serverStateColor
import net.turbovadim.bisquithost.network.Requests.FormattedServerStats
import net.turbovadim.bisquithost.network.Requests.formatServerStats
import net.turbovadim.bisquithost.network.Requests.getServerStats
import net.turbovadim.bisquithost.network.ServerAttributes
import net.turbovadim.bisquithost.ui.theme.Gray200

@Composable
fun NarrowServerCard(navController: NavController) {
    LazyVerticalGrid(
        modifier = Modifier.padding(horizontal = 2.5.dp),
        columns = GridCells.Fixed(2),
    ) {
        items(serverAttributes) { serverAttributes ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Gray200),
                shape = RoundedCornerShape(35.dp),
                modifier = Modifier
//                    .height(140.dp)
                    .fillMaxWidth()
                    .clickable {
                        if (!serverAttributes.isSuspended) {
                            navController.navigate(ScreensList.ServerControlScreen.withArgs(
                                serverAttributes.id,
                                serverAttributes.name,
                                serverAttributes.ram,
                                serverAttributes.cpu,
                                serverAttributes.disk,
                                serverAttributes.sftpUrl,
                                serverAttributes.backupsLimit,
                            ))
                        }

                    }
                    .padding(
                        horizontal = 4.dp,
                        vertical = 4.dp
                    ),
            ) {
                NarrowServerCardData(serverAttributes)
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NarrowServerCardData(serverAttributes: ServerAttributes) {
    val scope = rememberCoroutineScope()

    val serverStats = remember { mutableStateOf(FormattedServerStats("", 0, 0, 0)) }
    if (!serverAttributes.isSuspended) {
        scope.launch(Dispatchers.IO) {
            serverStats.value = formatServerStats(getServerStats(serverAttributes.id))
        }
    } else {
        serverStats.value.current_state = "offline"
    }
    Crossfade(targetState = serverStats.value.current_state.isNotBlank(), label = "") { state ->
        if (state) {
                Column(
                    modifier = Modifier.padding(top = 4.dp, bottom = 0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            fontSize = 17.sp,
                            modifier = Modifier.padding(end = 7.dp),
                            fontWeight = FontWeight.W500,
                            text = serverAttributes.name
                        )
                        Canvas(modifier = Modifier
                            .width(5.5.dp)
                            .height(5.5.dp)
                            .offset(y = 1.5.dp)
//                .border(2.dp, Color.LightGray, RoundedCornerShape(20.dp))
                        ) {
                            drawCircle(
                                color = serverStateColor(serverStats.value.current_state),
                                radius = size.minDimension
                            )
                        }
                    }
                    if (serverAttributes.isSuspended) {
                        Box(
                            modifier = Modifier
                                .height(100.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .offset(y = -3.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(Color.Red)
                            ) {
                                Text(
                                    text = "Заморожен",
                                    modifier = Modifier.padding(5.dp),
                                    color = Color.White,
                                    fontWeight = FontWeight.W600
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.padding(bottom = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CustomCircularProgress(
                                canvasSize = 55.dp,
                                withDecimal = true,
                                indicatorValue = serverStats.value.ram * 10 / 1024,
                                maxIndicatorValue = serverAttributes.ram * 10 / 1024 + 1,
                                backgroundIndicatorColor = Color(0x33000000),
                                backgroundIndicatorStrokeWidth = 16f,
                                foregroundIndicatorStrokeWidth = 16f,
                                bigTextSuffix = "GB",
                                bigTextFontSize = 12.sp,
                                bigTextColor = Color.White,
                                smallText = "RAM",
                                smallTextFontSize = 11.sp,
                                smallTextColor = Color.White
                            )
//                            Spacer(Modifier.width(10.dp))
                            CustomCircularProgress(
                                canvasSize = 55.dp,
                                withDecimal = false,
                                indicatorValue = serverStats.value.cpu,
                                maxIndicatorValue = serverAttributes.cpu,
                                backgroundIndicatorColor = Color(0x33000000),
                                backgroundIndicatorStrokeWidth = 16f,
                                foregroundIndicatorStrokeWidth = 16f,
                                bigTextSuffix = "%",
                                bigTextFontSize = 12.sp,
                                bigTextColor = Color.White,
                                smallText = "CPU",
                                smallTextFontSize = 11.sp,
                                smallTextColor = Color.White
                            )
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .offset(y = (-5).dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                fontSize = 13.sp,
                                modifier = Modifier.offset(x = 10.dp, y = (-1).dp),
                                text = "0",
                                fontWeight = FontWeight.W600,
                            )
                            Column(
                                Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val diskUsePercentage: Float = ((serverStats.value.disk.toFloat() / serverAttributes.disk))
                                Text(
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    text = "SSD Хранилище",
                                    modifier = Modifier
                                        .padding(bottom = 1.dp)
                                )
                                LinearProgressIndicator(
                                    color = Color(0xCC1AB641),
                                    trackColor = Color(0xCC444444),
                                    progress = diskUsePercentage,
                                    modifier = Modifier
                                        .height(6.dp)
                                        .fillMaxWidth(0.9f)
                                        .padding(start = 8.dp, end = 8.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                val diskGbUsed = (serverStats.value.disk.toDouble() / 1024).roundTo(1)
                                Text(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.W500,
                                    text = diskGbUsed.toString() +
                                            "GB / " +
                                            (diskUsePercentage.toDouble() * 100).roundTo(1).toString() +
                                            "%"
                                )
                            }
                            Text(
                                fontSize = 13.sp,
                                modifier = Modifier.offset(x = (-10).dp, y = (-1).dp),
                                fontWeight = FontWeight.W600,
                                text = (serverAttributes.disk / 1024).toString())
                        }
                    }
                }
        } else {
            Box(
                modifier = Modifier
                    .height(127.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.Black
                )
            }
        }
    }
}
