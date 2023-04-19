package net.turbovadim.bisquithost.Screens.ServerControl.Backups

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MoveToInbox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.turbovadim.bisquithost.ServerId

@Composable
fun BackupCard(
    item: BackupArrayItem,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 2.5.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (item.is_completed) {
                    Row(
                        Modifier
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(horizontal = 8.dp),
                            imageVector = Icons.Rounded.MoveToInbox,
                            contentDescription = "",
                            tint = Color(0xFFECBE17)
                        )
                        Text(
                            text = item.name,
                            color = Color.White,
                            maxLines = 1,
                        )
                    }
                    Row(
                        Modifier
                            .weight(0.1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            BackupDropdown(item)
                        }
                    }
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        CircularProgressIndicator(
                            color = Color.White
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                val size = if ( (item.bytes.toDouble() / 1024 / 1024) < 1024 ) {
                    (item.bytes.toDouble() / 1024 / 1024).toInt().toString() + " MB"
                } else {
                    (item.bytes.toDouble() / 1024 / 1024 / 1024).toInt().toString() + " GB"
                }
                Text(
                    modifier = Modifier.offset(y = (-9).dp),
                    text = "Размер: $size",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun BackupDropdown(
    item: BackupArrayItem,
) {
    val openDeleteAlert = remember { mutableStateOf(false) }
    DeleteBackupAlert(openDialog = openDeleteAlert, item = item)
    val backupsVM: BackupsVM = viewModel()
    val scope = rememberCoroutineScope()
    val dropdownState = remember { mutableStateOf(false) }
    IconButton(onClick = { dropdownState.value = true }) {
        Icon(
            modifier = Modifier.padding(end = 5.dp),
            imageVector = Icons.Rounded.MoreVert,
            contentDescription = "",
            tint = Color.White
        )
        val uriHandler = LocalUriHandler.current
        DropdownMenu(
            expanded = dropdownState.value,
            onDismissRequest = { dropdownState.value = false }
        ) {
            DropdownMenuItem(
                modifier = Modifier
                    .height(18.dp),
                text = {
                    Text(
                        text = "Скачать",
                        modifier = Modifier.padding(end = 5.dp)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Download,
                        contentDescription = ""
                    )
                },
                onClick = {
                    dropdownState.value = false
                    scope.launch(Dispatchers.IO) {
                        uriHandler.openUri(backupsVM.downloadBackup(ServerId, item.uuid))
                    }
                }
            )
            Divider(Modifier.padding(vertical = 7.dp))
            DropdownMenuItem(
                modifier = Modifier
                    .height(18.dp),
                text = {
                    Text(
                        text = "Удалить",
                        modifier = Modifier.padding(end = 5.dp)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = ""
                    )
                },
                onClick = {
                    dropdownState.value = false
                    openDeleteAlert.value = true
                }
            )
        }
    }
}

@Composable
fun DeleteBackupAlert(openDialog: MutableState<Boolean>, item: BackupArrayItem) {
    if (openDialog.value) {
        val backupsVM: BackupsVM = viewModel()
        val scope = rememberCoroutineScope()
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(
                    text = "Удалить Бэкап",
                    fontSize = 23.sp
                )
            },
            text = {
                Text(
                    "вы уверены что хотите удалить '${item.name}'? Это действие нельзя отменить"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        scope.launch(Dispatchers.IO) {
                            val isCompleted = backupsVM.deleteBackup(ServerId, item.uuid)
                            if (isCompleted) {
                                backupsVM.reloadBackupsListFun()
                            }
                        }
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}