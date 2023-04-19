package net.turbovadim.bisquithost.Screens.ServerControl.FileManager

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.turbovadim.bisquithost.ServerId
import net.turbovadim.bisquithost.network.*
import net.turbovadim.bisquithost.ui.theme.Gray200
import net.turbovadim.bisquithost.ui.theme.LightBlue300

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FilesContainer(
    navController: NavController,
    directory: String
) {
    val openFolderAlert = remember { mutableStateOf(false) }
    val fileManagerVM: FileManagerVM = viewModel()
    var aboba by remember { mutableStateOf(listOf<FileManagerAttributes>()) }
    val currentFolder = remember { mutableStateOf(directory) }

    LaunchedEffect(key1 = currentFolder.value, key2 = fileManagerVM.refreshFiles.value) {
        launch(Dispatchers.IO) {
            aboba = getFiles(ServerId, currentFolder.value)
        }
    }
    CreateFolderAlert(openDialog = openFolderAlert, currentFolder.value)
    LazyColumn(
        Modifier.padding(bottom = 5.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (currentFolder.value != "") {
                            var preload = currentFolder.value
                            preload = preload.dropLast(1)
                            preload = preload.dropLastWhile {
                                it != '/'
                            }
                            currentFolder.value = preload
                        }
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
                        text = "/${currentFolder.value}",
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }
                Row(
                    Modifier.padding(start = 5.dp, end = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f),
                        onClick = {
                              openFolderAlert.value = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LightBlue300),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = "Создать Папку",
                            fontWeight = FontWeight.W500,
                            color = Color.White,
                        )
                    }


//                    val pickPictureLauncher = rememberLauncherForActivityResult(
//                        ActivityResultContracts.GetContent()
//                    ) { fileUri ->
//                        if (fileUri != null) {
//                            println(File(fileUri.path!!))
//                            scope.launch(Dispatchers.IO) {
//                                uploadFile(getSignedUrl(ServerId), fileUri.toFile())a
//                            }
//                        }
//                    }
                    Button(
                        modifier = Modifier
                            .weight(1f),
                        onClick = {
//                            val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
//                            pdfIntent.type = "application/pdf"
//                            pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
//                            startActivityForResult(,pdfIntent, 12)

//                            pickPictureLauncher.launch("*/*")
                            },
                        colors = ButtonDefaults.buttonColors(containerColor = LightBlue300),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = "Загрузить Файл",
                            fontWeight = FontWeight.W500,
                            color = Color.White
                        )
                    }
                }
            }
        }

        itemsIndexed(aboba) { _, aboba ->
            FileCard(
                navController = navController,
                currentFolder = currentFolder,
                aboba = aboba
            )
        }
    }
}

@Composable
fun FileCard(
    navController: NavController,
    currentFolder: MutableState<String>,
    aboba: FileManagerAttributes,
) {
    Card(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp, top = 3.dp)
            .fillMaxWidth()
            .clickable {
                if (!aboba.is_file) {
                    currentFolder.value = currentFolder.value + aboba.name + "/"
                }
                if (aboba.is_image) {
                    println(currentFolder.value + aboba.name)
                    navController.navigate(FileManagerScreens.ViewImage.route + "?file=${aboba.name}&directory=${currentFolder.value}")
                }
                if (aboba.is_editable) {
                    navController.navigate(FileManagerScreens.EditFile.route + "?file=${aboba.name}&directory=${currentFolder.value}")
                }
            },
        colors = CardDefaults.cardColors(containerColor = Gray200)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    imageVector = aboba.file_icon,
                    contentDescription = "icon",
                    tint = aboba.icon_color
                )
                Text(
                    text = aboba.name,
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
                FilesDropdown(aboba, currentFolder.value)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesDropdown(aboba: FileManagerAttributes, currentFolder: String) {
    val openDeleteAlert = remember { mutableStateOf(false) }
    val openRenameAlert = remember { mutableStateOf(false) }
    DeleteAlert(openDialog = openDeleteAlert, currentFolder, aboba.name)
    RenameAlert(openDialog = openRenameAlert, currentFolder, aboba.name)
    var isExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
        IconButton(
            onClick = { isExpanded = true },
        ) {
            Icon(
                modifier = Modifier.padding(end = 5.dp),
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = "icon",
                tint = Color.White
            )
            val uriHandler = LocalUriHandler.current
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
            ) {
                if (aboba.is_file) {
                    DropdownMenuItem(
                        modifier = Modifier
                            .height(18.dp),
                        text = { Text( text = "Скачать" ) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Download,
                                contentDescription = ""
                            )
                        },
                        onClick = {
                            isExpanded = false
                            scope.launch(Dispatchers.IO) {
                                uriHandler.openUri(downloadFile(ServerId, currentFolder, aboba.name))
                            }
                        }
                    )
                    Divider(Modifier.padding(vertical = 7.dp))
                }
                DropdownMenuItem(
                    modifier = Modifier
                        .height(18.dp),
                    text = { Text( text = "Переименовать" ) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = ""
                        )
                    },
                    onClick = {
                        isExpanded = false
                        scope.launch(Dispatchers.IO) {
                            openRenameAlert.value = true
                        }
                    }
                )
                Divider(Modifier.padding(vertical = 7.dp))
                DropdownMenuItem(
                    modifier = Modifier
                        .height(18.dp),
                    text = { Text( text = "Удалить" ) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = ""
                        )
                    },
                    onClick = {
                        isExpanded = false
                        scope.launch(Dispatchers.IO) {
                            openDeleteAlert.value = true
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFolderAlert(openDialog: MutableState<Boolean>, currentFolder: String) {
    if (openDialog.value) {
        val fileManagerVM: FileManagerVM = viewModel()
        val scope = rememberCoroutineScope()
        val texts1 = remember {
            mutableStateOf("")
        }
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(
                    text = "Введите название папки",
                    fontSize = 23.sp
                )
            },
            text = {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = texts1.value,
                        onValueChange = {texts1.value = it}
                    )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        scope.launch(Dispatchers.IO) {
                            val isCompleted = createFolder(ServerId, currentFolder, texts1.value)
                            if (isCompleted) {
                                fileManagerVM.callRefreshFiles()
                            }
                        }
                    }
                ) {
                    Text("Создать")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameAlert(openDialog: MutableState<Boolean>, currentFolder: String, file: String) {
    if (openDialog.value) {
        val fileManagerVM: FileManagerVM = viewModel()
        val scope = rememberCoroutineScope()
        val texts1 = remember {
            mutableStateOf("")
        }
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
//            icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
            title = {
                Text(
                    text = "Переименовать Файл/Папку",
                    fontSize = 23.sp
                )
            },
            text = {
                OutlinedTextField(
                    value = texts1.value,
                    onValueChange = {texts1.value = it}
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        scope.launch(Dispatchers.IO) {
                            val isCompleted = renameFile(ServerId, currentFolder, file, texts1.value)
                            if (isCompleted) {
                                fileManagerVM.callRefreshFiles()
                            }
                        }
                    }
                ) {
                    Text("Переименовать")
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

@Composable
fun DeleteAlert(openDialog: MutableState<Boolean>, currentFolder: String, file: String) {
    if (openDialog.value) {
        val fileManagerVM: FileManagerVM = viewModel()
        val scope = rememberCoroutineScope()
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(
                    text = "Удалить Файл/Папку",
                    fontSize = 23.sp
                )
            },
            text = {
                Text(
                "вы уверены что хотите удалить '$file'? Это действие нельзя отменить"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        scope.launch(Dispatchers.IO) {
                            val isCompleted = deleteFile(ServerId, currentFolder, file)
                            if (isCompleted) {
                                fileManagerVM.callRefreshFiles()
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