package net.turbovadim.bisquithost.Screens.ServerControl.FileManager

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.turbovadim.bisquithost.ServerId
import net.turbovadim.bisquithost.network.editFileContent
import net.turbovadim.bisquithost.network.getFileContent
import net.turbovadim.bisquithost.ui.theme.SemiWhite100

@SuppressLint("RememberReturnType")
@Composable
fun EditFile(
    navController: NavController,
    directory: String,
    file: String
) {
    val fileContent = remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                scope.launch(Dispatchers.IO) {
                    fileContent.value = getFileContent(ServerId, directory, file)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        Modifier
            .background(Color(0xFF1B1B1B))
            .fillMaxSize()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color.Black),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                navController.navigate(FileManagerScreens.FileManager.route + "?directory=$directory")
            }
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "",
                    tint = Color.White
                )
            }
            Text(
                modifier = Modifier.offset(y = (-1).dp),
                text = "/$directory$file",
                color = Color.White,
                fontSize = 15.sp,
                maxLines = 1
            )
            IconButton(onClick = {
                editFileContent(ServerId, directory, file, fileContent.value)
            }) {
                Icon(
                    imageVector = Icons.Rounded.Save,
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }
        BasicTextField(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 5.dp, start = 5.dp, end = 5.dp),
            value = fileContent.value,
            onValueChange = {
                fileContent.value = it
            },
            textStyle = TextStyle(color = SemiWhite100),
        )

    }
}





