package com.example.ninhdt_btvn.ui.screen.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ninhdt_btvn.R
import com.example.ninhdt_btvn.ui.screen.main.component.LoadingDialog
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    onBackClick: () -> Unit = {},
    viewModel: ResultViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(imageUrl) {
        viewModel.updateImageUrl(imageUrl)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_result_back),
                                contentDescription = "Back",
                            )
                        }
                    },
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                ) {
                    Spacer(modifier = Modifier.weight(0.5f))
                    PhotoArea(
                        imageUrl = state.imageUrl,
                    )
                    if (state.errorMessage != null) {
                        Text(
                            text = state.errorMessage ?: "",
                            color = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.5f))
                    DownloadButton(
                        onClick = { viewModel.downloadImage() },
                        enabled = !state.isLoading && !state.isDownloading && state.imageUrl != null
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        )

        if (state.isDownloading) {
            LoadingDialog(R.string.result_loading)
        }
    }
}

@Composable
fun PhotoArea(
    imageUrl: String?,
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
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            imageUrl != null -> {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Generated image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )
            }
            else -> {
                Image(
                    painter = painterResource(id = R.drawable.ic_main_image),
                    contentDescription = "No image",
                    modifier = Modifier.size(80.dp),
                )
            }
        }
    }
}

@Composable
fun DownloadButton(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
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
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled
    ) {
        Text(
            text = stringResource(R.string.result_button),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    ResultScreen()
}