package net.turbovadim.bisquithost.Screens.StartingPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.turbovadim.bisquithost.R
import net.turbovadim.bisquithost.ui.theme.*


@Composable
fun FirstPage(navController: NavController) {
    GuideContent(
        header = "Где найти API-Ключ",
        step = "Шаг 1",
        image = painterResource(id = R.drawable.second),
        guideText = "Перейдя по ссылке, выполните вход в свой аккаунт и откройте настройки профиля",
        onBackClick = { },
        onForwardClick = { navController.navigate(GuideSteps.SecondPage.route) },
        backButtonDisabled = true
    )
}

@Composable
fun SecondPage(navController: NavController) {
    GuideContent(
        header = "Где найти API-Ключ",
        step = "Шаг 2",
        image = painterResource(id = R.drawable.second),
        guideText = """Перейдите во вкладку "API" в профиле""",
        onBackClick = { navController.navigate(GuideSteps.FirstPage.route) },
        onForwardClick = { navController.navigate(GuideSteps.ThirdPage.route) },
    )
}

@Composable
fun ThirdPage(navController: NavController) {
    GuideContent(
        header = "Где найти API-Ключ",
        step = "Шаг 3",
        image = painterResource(id = R.drawable.third),
        guideText = "Введите любое название для API-ключа и нажмите кнопку «Создать». Затем сохраните ваш ключ в буфер обмена",
        onBackClick = { navController.navigate(GuideSteps.SecondPage.route) },
        onForwardClick = { navController.navigate(GuideSteps.MainPage.route) },
    )
}

@Composable
fun GuideContent(
    header: String,
    step: String,
    image: Painter,
    guideText: String,
    onForwardClick: () -> Unit,
    onBackClick: () -> Unit,
    forwardButtonDisabled: Boolean = false,
    backButtonDisabled: Boolean = false,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(top = 30.dp),
            text = header,
            fontSize = 18.sp,
            fontFamily = GoghBold,
            color = Color.White,
        )
        Text(
            modifier = Modifier
                .padding(bottom = 30.dp),
            text = step,
            color = Color.White,
            fontSize = 14.sp,
            fontFamily = GoghBold,
        )
        Image(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .width(250.dp),
            painter = image,
            contentDescription = "",
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 35.dp)
                .padding(vertical = 15.dp),
            text = guideText,
            color = Color.White,
            fontFamily = FontFamily.SansSerif,
            fontSize = 15.sp,
            fontWeight = FontWeight.W500
        )
        GuideNavButtons(
            onForwardClick = onForwardClick,
            onBackClick = onBackClick,
            forwardButtonDisabled = forwardButtonDisabled,
            backButtonDisabled = backButtonDisabled,
        )
    }
}

@Composable
fun GuideNavButtons(
    onForwardClick: () -> Unit,
    onBackClick: () -> Unit,
    forwardButtonDisabled: Boolean,
    backButtonDisabled: Boolean,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier
                .clip(CircleShape)
                .alpha(if (backButtonDisabled) { 0.5f } else { 1f }),
            onClick = onBackClick,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LightBlue300)
        ) {
            Icon(Icons.Rounded.ArrowBack, contentDescription = "")
        }
        LinkAndSpacers()
        Button(
            modifier = Modifier
                .clip(CircleShape)
                .alpha(if (forwardButtonDisabled) { 0.5f } else { 1f }),
            onClick = onForwardClick,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LightBlue300)
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowForward,
                contentDescription = "",
                tint = Color.White
            )
        }
    }
}

@Composable
fun LinkAndSpacers() {
    val uriHandler = LocalUriHandler.current
    Spacer(Modifier.width(40.dp))
    Button(
        modifier = Modifier
            .clip(CircleShape),
        contentPadding = PaddingValues(horizontal = 15.dp),
        onClick = {
            uriHandler.openUri("https://mgr.bisquit.host/")
        },
        colors = ButtonDefaults.buttonColors(containerColor = LightBlue200)
    ) {
        Text(text = "Ссылка")
        Icon(
            imageVector = Icons.Rounded.Link,
            contentDescription = "",
            tint = Color.White
        )
    }
    Spacer(Modifier.width(40.dp))
}