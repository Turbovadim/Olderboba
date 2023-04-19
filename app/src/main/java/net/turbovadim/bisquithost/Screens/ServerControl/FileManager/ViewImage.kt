package net.turbovadim.bisquithost.Screens.ServerControl.FileManager

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.turbovadim.bisquithost.ServerId
import net.turbovadim.bisquithost.network.downloadFile

@Composable
fun ViewImage(
    navController: NavController,
    directory: String,
    file: String
) {
    var imageUrl by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                scope.launch(Dispatchers.IO) {
                    imageUrl = downloadFile(ServerId, directory, file)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    Column {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                navController.navigate(FileManagerScreens.FileManager.route + "?directorys=$directory")
            }
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "",
                    tint = Color.White
                )
            }
            Text(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .offset(y = (-1).dp),
                text = "home/container/",
                color = Color(0xFF616161),
                fontSize = 15.sp
            )
            Text(
                modifier = Modifier.offset(y = (-1).dp),
                text = directory + file,
                color = Color.White,
                fontSize = 15.sp,
                maxLines = 1
            )
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            AsyncImage(
                modifier = Modifier.fillMaxWidth(),
                model = imageUrl,
                contentDescription = "",
            )
        }
    }
}