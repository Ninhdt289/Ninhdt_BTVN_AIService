package com.example.ninhdt_btvn.ui.screen.main

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aisevice.data.local.impl.ImageRepositoryImpl
import com.example.aisevice.data.remote.impl.StyleRepositoryImpl
import com.example.aisevice.data.remote.model.Config
import com.example.aisevice.data.remote.model.StyleCategory
import com.example.aisevice.data.remote.model.StyleItem
import com.example.ninhdt_btvn.R
import com.example.ninhdt_btvn.data.repository.ImageUploadRepositoryImpl
import com.example.ninhdt_btvn.ui.screen.main.component.GenerateButton
import com.example.ninhdt_btvn.ui.screen.main.component.LoadingDialog
import com.example.ninhdt_btvn.ui.screen.main.component.PhotoUploadArea
import com.example.ninhdt_btvn.ui.screen.main.component.PromptInputField
import com.example.ninhdt_btvn.ui.screen.main.component.StyleItemCard
import com.example.ninhdt_btvn.ui.screen.main.component.StyleList
import com.example.ninhdt_btvn.ui.screen.main.component.StyleSelectionPlaceholder
import com.example.ninhdt_btvn.ui.screen.main.component.StyleSelectionSection
import com.example.ninhdt_btvn.ui.screen.main.component.StyleTabsWithContent
import com.example.ninhdt_btvn.utils.PermissionUtils
import org.koin.androidx.compose.koinViewModel

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Composable
fun MainScreen(
    imageUri: String? = null,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel(),
    onOpenPickPhoto: () -> Unit = {},
    onImageSelected: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val permissionLauncher = PermissionUtils.rememberPermissionLauncher(
        onPermissionGranted = onOpenPickPhoto,
        onPermissionDenied = { }
    )
    val errorMessage = stringResource(R.string.internet_connection_error)
    var hasLoadedStyle by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!hasLoadedStyle && isNetworkAvailable(context)) {
            viewModel.onEvent(MainUIEvent.ReloadStyles)
            hasLoadedStyle = true
        } else {
            viewModel.onEvent(MainUIEvent.SetError(errorMessage))
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        OnNetworkAvailable {
            if (state.availableStyles.isNullOrEmpty() && isNetworkAvailable(context)) {
                viewModel.onEvent(MainUIEvent.ReloadStyles)
            }
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PromptInputField(
                value = state.promptText,
                onValueChange = { viewModel.onEvent(MainUIEvent.UpdatePromptText(it)) },
                onClearClick = { viewModel.onEvent(MainUIEvent.UpdatePromptText("")) }
            )

            PhotoUploadArea(
                onChangeImage = {
                    if (PermissionUtils.hasImagePermissions(context)) {
                        onOpenPickPhoto()
                    } else {
                        permissionLauncher.launch(PermissionUtils.getRequiredPermissions())
                    }
                },
                selectedImage = imageUri
            )

            if (!state.availableStyles.isNullOrEmpty()) {
                StyleSelectionSection(
                    styleList = state.availableStyles,
                    selectedStyle = state.selectedStyle,
                    onStyleSelected = { style ->
                        viewModel.onEvent(MainUIEvent.SelectStyle(style))
                    }
                )
            } else {
                StyleSelectionPlaceholder()
            }
            GenerateButton(
                onClick = {
                    Log.d("GenerateButton", "Button clicked")
                    viewModel.onEvent(MainUIEvent.GenerateImage(imageUri, onImageSelected))
                },
                enabled = state.selectedStyle != null && imageUri != null
            )
            Spacer(modifier = Modifier.height(50.dp))
        }

        if (state.isGenerating) {
            LoadingDialog(R.string.main_loading)
        }

        state.errorMessage?.let { errorMsg ->
            LaunchedEffect(errorMsg) {
                kotlinx.coroutines.delay(2000)
                viewModel.onEvent(MainUIEvent.ClearError)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color(0xFFD32F2F))
                    .padding(vertical = 12.dp),
            ) {
                Text(
                    text = errorMsg,
                    color = Color.White,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(horizontal = 10.dp)
                )
            }
        }
    }
}

@Composable
fun OnNetworkAvailable(
    onAvailable: () -> Unit
) {
    val context = LocalContext.current
    val connectivityManager = remember {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    val callback = rememberUpdatedState(onAvailable)

    DisposableEffect(Unit) {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                callback.value()
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PromptInputFieldPreview() {
    PromptInputField(
        value = "Sample prompt text",
        onValueChange = {},
        onClearClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun DialogPreview() {
    LoadingDialog(R.string.main_loading)
}


@Preview(showBackground = true)
@Composable
fun PhotoUploadAreaPreview() {
    PhotoUploadArea(
        onChangeImage = {},
        selectedImage = null
    )
}

@Preview(showBackground = true)
@Composable
fun StyleItemCardPreview() {
    StyleItemCard(
        styleItem = StyleItem(
            id = "1",
            name = "Style 1",
            key = "https://example.com/image1.jpg",
            config = Config(
                positivePrompt = "good lighting",
                negativePrompt = "blurry",
            ),
        ),
        isSelected = true,
        onSelect = {}
    )
}

@Preview(showBackground = true)
@Composable
fun StyleListPreview() {
    val sampleStyles = listOf(
        StyleItem(
            id = "1",
            name = "Style 1",
            key = "https://example.com/image1.jpg",
            config = Config(

                positivePrompt = "good lighting",
                negativePrompt = "blurry",

                ),

            ),
        StyleItem(
            id = "2",
            name = "Style 2",
            key = "https://example.com/image2.jpg",
            config = Config(
                positivePrompt = "sharp focus",
                negativePrompt = "noise",
            )
        )
    )

    StyleList(
        styles = sampleStyles,
        selectedStyle = sampleStyles[0],
        onStyleSelected = {},
        listState = rememberLazyListState()
    )
}

@Preview(showBackground = true)
@Composable
fun StyleTabsWithContentPreview() {
    val sampleCategories = listOf(
        StyleCategory(
            name = "Category 1",
            styles = listOf(
                StyleItem(
                    id = "1",
                    name = "Style 1",
                    key = "https://example.com/image1.jpg",
                    config = Config(
                        positivePrompt = "good lighting",
                        negativePrompt = "blurry",
                    )
                ),

                StyleItem(
                    id = "2",
                    name = "Style 2",
                    key = "https://example.com/image2.jpg",
                    config = Config(
                        positivePrompt = "sharp focus",
                        negativePrompt = "noise",
                    ),
                )
            ),
        ),
        StyleCategory(
            name = "Category 2",
            styles = listOf(
                StyleItem(
                    id = "1",
                    name = "Style 1",
                    key = "https://example.com/image1.jpg",
                    config = Config(
                        positivePrompt = "good lighting",
                        negativePrompt = "blurry",
                    ),

                    ),
                StyleItem(
                    id = "2",
                    name = "Style 2",
                    key = "https://example.com/image2.jpg",
                    config = Config(

                        positivePrompt = "sharp focus",
                        negativePrompt = "noise",

                        ),

                    )
            ),

            )
    )
    StyleTabsWithContent(
        styleList = sampleCategories,
        selectedStyle = sampleCategories[0].styles[0],
        onStyleSelected = {}
    )
}

@Preview(showBackground = true)
@Composable
fun StyleSelectionSectionPreview() {
    val sampleCategories = listOf(
        StyleCategory(
            name = "Category 1",
            styles = listOf(
                StyleItem(
                    id = "1",
                    name = "Style 1",
                    key = "https://example.com/image1.jpg",
                    config = Config(

                        positivePrompt = "good lighting",
                        negativePrompt = "blurry",

                        ),
                ),
                StyleItem(
                    id = "2",
                    name = "Style 2",
                    key = "https://example.com/image2.jpg",
                    config = Config(
                        positivePrompt = "sharp focus",
                        negativePrompt = "noise",
                    )
                )
            ),
        )
    )

    StyleSelectionSection(
        styleList = sampleCategories,
        selectedStyle = sampleCategories[0].styles[0],
        onStyleSelected = {}
    )
}

@Preview(showBackground = true)
@Composable
fun GenerateButtonPreview() {
    GenerateButton(onClick = {})
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val styleRepo = StyleRepositoryImpl()
    val ImageRepo = ImageRepositoryImpl(LocalContext.current.contentResolver)
    val uploadRepositoryImpl = ImageUploadRepositoryImpl(LocalContext.current)
    val viewModel = MainViewModel(styleRepo,uploadRepositoryImpl,ImageRepo)
    MainScreen(viewModel = viewModel)
}