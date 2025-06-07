package com.example.ninhdt_btvn.ui.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ninhdt_btvn.R
import com.example.ninhdt_btvn.data.remote.model.StyleResponse
import com.example.ninhdt_btvn.data.remote.repository.StyleRepository

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var promptText by remember { mutableStateOf("") }
    val repository = StyleRepository()
    var stylesResult by remember { mutableStateOf<Result<StyleResponse>?>(null) }

    LaunchedEffect(Unit) {
        stylesResult = repository.getStyles()
        promptText = "Fetched styles: ${stylesResult?.getOrNull()?.items}"
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PromptInputField(
            value = promptText,
            onValueChange = { promptText = it },
            onClearClick = { promptText = "" }
        )

        PhotoUploadArea(onChangeImage = {})
        StyleSelectionSection()
        Spacer(modifier = Modifier.weight(1f))
        GenerateButton()
        Spacer(modifier = Modifier.height(50.dp))

    }
}

@Composable
fun PromptInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onClearClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .border(
                width = 2.dp,
                color = Color(0xFFE400D9),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(R.string.main_edittext_hint),
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            )
        }

        Image(
            painter = painterResource(id = R.drawable.ic_main_delete),
            contentDescription = "Clear text",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
                .clickable { onClearClick() }
                .padding(4.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun PhotoUploadArea(
    onChangeImage: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .border(
                width = 2.dp,
                color = Color(0xFFE400D9),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {  },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_main_image),
                contentDescription = "Add photo",
                modifier = Modifier.size(80.dp),
            )

            Text(
                text = stringResource(R.string.main_image_title),
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_main_change_image),
            contentDescription = "Clear text",
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(50.dp)
                .clickable { onChangeImage() }
                .padding(top = 15.dp , start = 19.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun StyleSelectionSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.main_style_title),
            color = Color(0xFFE400D9),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        StyleList()
    }
}

@Composable
fun StyleList() {
    val styleItems = listOf(
        StyleItem("Novelistic", "style_1"),
        StyleItem("Novelistic", "style_2"),
        StyleItem("Novelistic", "style_3"),
        StyleItem("Novelistic", "style_4"),
        StyleItem("Realistic", "style_5")
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(styleItems) { item ->
            StyleItemCard(styleItem = item)
        }
    }
}

@Composable
fun StyleItemCard(styleItem: StyleItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { /* Handle style selection */ }
        ) {
            // Placeholder for style image - replace with actual image resource
            // Image(
            //     painter = painterResource(id = R.drawable.${styleItem.imageResource}),
            //     contentDescription = styleItem.name,
            //     modifier = Modifier.fillMaxSize(),
            //     contentScale = ContentScale.Crop
            // )

            // Temporary placeholder background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            )
        }

        Text(
            text = styleItem.name,
            fontSize = 12.sp,
            color = Color.Black
        )
    }
}

@Composable
fun GenerateButton() {
    Button(
        onClick = {  },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFE400D9),
                        Color(0xFF1D00F5)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = stringResource(R.string.main_button),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

data class StyleItem(
    val name: String,
    val imageResource: String
)

@Preview(showBackground = true)
@Composable
fun GenerateButtonPreview() {
    MainScreen()
}