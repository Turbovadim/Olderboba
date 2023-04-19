package net.turbovadim.bisquithost.Components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import net.turbovadim.bisquithost.ui.theme.SemiWhite100
import net.turbovadim.bisquithost.ui.theme.SemiWhite200

@Composable
@Preview
fun ErrorTest() {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(key1 = "a") {
        delay(3000)
        visible.value = true
        delay(5500)
        visible.value = false
    }
    ErrorAlert(
        error = "Aboba Aboba Aboba Aboba Aboba Aboba Aboba Aboba Aboba Aboba Aboba Aboba Aboba Aboba Aboba Aboba Aboba Aboba Aboba Aboba",
        visible = visible.value
    )
}

@Composable
fun ErrorAlert(error: String, visible: Boolean) {
    val load = remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val animateLoad = animateFloatAsState(
        targetValue = (if (load.value) 1f else 0f),
        animationSpec = tween(5000, easing = LinearEasing),
        label = ""
    )
    LaunchedEffect(key1 = "a") {
        delay(3000)
        load.value  = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { with(density) { -40.dp.roundToPx() } },
        exit = slideOutVertically { with(density) { -81.dp.roundToPx() } }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFC53B3B)
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp)
                            .align(Alignment.CenterVertically),
                        verticalArrangement = Arrangement.Center
                    ) {
//                    Spacer(modifier = Modifier.height(20.dp))
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "",
                            tint = SemiWhite100
                        )
                    }
                    Text(
                        text = error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp, end = 10.dp, bottom = 5.dp),
                        color = SemiWhite100
                    )
                }
                LinearProgressIndicator(
                    progress = animateLoad.value,
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    trackColor = Color(0x2FFFFFFF),
                    color = SemiWhite200
                )
            }
        }
    }
}
