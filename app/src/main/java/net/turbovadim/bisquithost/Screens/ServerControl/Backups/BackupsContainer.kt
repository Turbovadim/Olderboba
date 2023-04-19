package net.turbovadim.bisquithost.Screens.ServerControl.Backups

import android.annotation.SuppressLint
import android.content.ClipData.Item
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.*
import net.turbovadim.bisquithost.BackupsLimit
import net.turbovadim.bisquithost.ServerId
import net.turbovadim.bisquithost.network.InitWebsocket
import net.turbovadim.bisquithost.ui.theme.Gray200
import net.turbovadim.bisquithost.ui.theme.LightBlue200
import net.turbovadim.bisquithost.ui.theme.LightBlue300

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BackupsContainer() {
    val backupsVM: BackupsVM = viewModel()
    val backupList = remember { mutableStateOf(BackupsData(null, null)) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(backupsVM.reloadBackupsList.value) {
        launch(Dispatchers.IO) {
            backupList.value = backupsVM.listBackups(ServerId)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        backupsVM.autoRefresh()
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                text = "Бэкапы",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.W500,
                textAlign = TextAlign.Center,
            )
        }
        if (backupList.value != BackupsData(null, null)) {
            if (!backupList.value.backups!!.isEmpty()) {
                itemsIndexed(backupList.value.backups!!) { _, item ->
                    BackupCard(item = item, color = Gray200)
                }
            }
            item {
                val padd = if (backupList.value.total_backups != 0) {
                    PaddingValues(bottom = 10.dp, top = 10.dp)
                } else {
                    PaddingValues(bottom = 10.dp)
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(padd),
                    text = "Создано ${backupList.value.total_backups} из $BackupsLimit бэкапов",
                    textAlign = TextAlign.Center,
                    color = Gray200
                )
            }
        }
        item {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        val isCompleted = backupsVM.createBackup(ServerId)
                        if (isCompleted) {
                            backupsVM.reloadBackupsListFun()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LightBlue300),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Создать бэкап")
            }
        }
    }
}