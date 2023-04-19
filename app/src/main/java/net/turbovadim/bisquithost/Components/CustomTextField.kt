package net.turbovadim.bisquithost.Components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        placeholder: String = "",
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        keyboardActions: KeyboardActions = KeyboardActions(),
        placeholderColor: Color = Color.Black,
        textStyle: TextStyle = TextStyle(color = Color(0xFF000000), fontSize = 15.sp)
    ) {
        Box (
            modifier = modifier
        ) {
            BasicTextField(
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                value = value,
                singleLine = true,
                textStyle = textStyle,
                onValueChange = onValueChange,
                decorationBox = { innerTextField ->
                    Row {
                        if (value.isBlank()) {
                            Text(
                                text = placeholder,
                                color = placeholderColor,
                                fontSize = 15.sp
                            )
                        } 
                    }
                innerTextField()
            }
        )
    }
}