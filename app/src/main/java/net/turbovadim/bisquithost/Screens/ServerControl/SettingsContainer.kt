package net.turbovadim.bisquithost.Screens.ServerControl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.turbovadim.bisquithost.*
import net.turbovadim.bisquithost.Components.CustomTextField
import net.turbovadim.bisquithost.network.RequestsVM
import net.turbovadim.bisquithost.ui.theme.Gray300
import net.turbovadim.bisquithost.ui.theme.Gray400
import net.turbovadim.bisquithost.ui.theme.LightBlue300
import net.turbovadim.bisquithost.ui.theme.SemiWhite200

@Composable
fun SettingsContainer() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        ChangeServerName()
    }
}

@Composable
fun ChangeServerName() {
    val requestsVM: RequestsVM = viewModel()
    val serverName = remember { mutableStateOf("") }
    val serverDescription = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Column(
        Modifier
            .padding(top = 10.dp)
    ) {
        Column(
            Modifier
                .padding(start = 5.dp, end = 5.dp, bottom = 10.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(Gray300),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .background(Gray400)
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                text = "Изменить имя сервера",
                fontSize = 20.sp,
                fontWeight = FontWeight.W500,
                textAlign = TextAlign.Center,
                color = SemiWhite200,
            )
            CustomTextField(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp, bottom = 10.dp, top = 10.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color(0xFF646464)),
                value = serverName.value,
                onValueChange = {serverName.value = it},
                placeholder = "Имя Сервера"
            )
            CustomTextField(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp, bottom = 5.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color(0xFF646464)),
                value = serverDescription.value,
                onValueChange = {serverDescription.value = it},
                placeholder = "Описание Сервера"
            )
            Button(
                modifier = Modifier.padding(bottom = 5.dp),
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        requestsVM.renameServer(ServerId, serverName.value, serverDescription.value)
                    }
                },
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = LightBlue300)
            ) {
                Text(
                    text = "Сохранить",
                    color = Color.Black
                )
            }
        }
        Column(
            Modifier
                .padding(horizontal = 5.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(Gray300),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .background(Gray400)
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                text = "SFTP Данные",
                fontSize = 20.sp,
                fontWeight = FontWeight.W500,
                textAlign = TextAlign.Center,
                color = SemiWhite200,
            )
            Text(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
                    .fillMaxWidth(),
                text = "АДРЕС СЕРВЕРА",
                color = Color.Black,
                textAlign = TextAlign.Start
            )
            CustomSelectableText(
                text = "sftp://${SftpUrl}:2022",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = Color(0xFF646464)),
            )
            Text(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
                    .fillMaxWidth(),
                text = "ИМЯ ПОЛЬЗОВАТЕЛЯ",
                color = Color.Black,
                textAlign = TextAlign.Start
            )
            CustomSelectableText(
                text = "${Username}.${ServerId}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp, bottom = 10.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(color = Color(0xFF646464)),
            )
        }
    }
}

@Composable
fun CustomSelectableText(
    text: String,
    modifier: Modifier = Modifier
) {
    Box (
        modifier = modifier
    ) {
        SelectionContainer {
            Text(
                modifier = Modifier
                    .padding(10.dp),
                text = text,
                color = Color.Black,
                fontSize = 15.sp
            )
        }
    }
}