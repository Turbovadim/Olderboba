package net.turbovadim.bisquithost.ServerCards

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import net.turbovadim.bisquithost.ui.theme.Gray200
import net.turbovadim.bisquithost.Screens.ServerList.roundTo
import net.turbovadim.bisquithost.Screens.ServerList.serverStateColor
import net.turbovadim.bisquithost.network.Requests.FormattedServerStats
import net.turbovadim.bisquithost.network.Requests.formatServerStats
import net.turbovadim.bisquithost.network.Requests.getServerStats

@Composable
fun WideServerCard(navController: NavController) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 2.5.dp)
    ) {
        items(TotalServers) { index ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Gray200),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
//                    .height(140.dp)
                    .fillMaxWidth()
                    .clickable {
                        if (!serverAttributes[index].isSuspended) {
                            navController.navigate(ScreensList.ServerControlScreen.withArgs(
                                serverAttributes[index].id,
                                serverAttributes[index].name,
                                serverAttributes[index].ram,
                                serverAttributes[index].cpu,
                                serverAttributes[index].disk,
                                serverAttributes[index].sftpUrl,
                                serverAttributes[index].backupsLimit,
                            ))
                        }

                    }
                    .padding(
                        horizontal = 4.dp,
                        vertical = 4.dp
                    ),
            ) {
                WideServerCardData(index)
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun WideServerCardData(index: Int) {
    val scope = rememberCoroutineScope()
    var serverStats = remember { mutableStateOf(FormattedServerStats("", 0, 0, 0)) }
    if (!serverAttributes[index].isSuspended) {
        scope.launch(Dispatchers.IO) {
            serverStats.value = formatServerStats(getServerStats(serverAttributes[index].id))
        }
    } else {
        serverStats.value.current_state = "offline"
    }


    Crossfade(targetState = serverStats.value.current_state.isNotBlank(), label = "") { state ->
        if (state) {
            if (serverAttributes[index].isSuspended) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(72.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontSize = 17.sp,
                            modifier = Modifier.padding(end = 7.dp),
                            fontWeight = FontWeight.W500,
                            text = serverAttributes[index].name)
                        Canvas(modifier = Modifier
                            .width(5.5.dp)
                            .height(5.5.dp)
                            .offset(y = 1.5.dp)
                        ) {
                            drawCircle(
                                color = serverStateColor(serverStats.value.current_state),
                                radius = size.minDimension
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
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
                }
            } else {

            }
        } else {
            Box(
                modifier = Modifier
                    .height(72.dp)
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