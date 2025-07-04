package com.example.ninhdt_btvn.ui.screen.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ninhdt_btvn.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun GenerateButton(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    var isClickable by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            if (isClickable) {
                isClickable = false
                onClick()
                coroutineScope.launch {
                    delay(1000)
                    isClickable = true
                }

            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        colorResource(id = R.color.primary_color),
                        colorResource(id = R.color.second_color)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled
    ) {
        Text(
            text = stringResource(R.string.main_button),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

