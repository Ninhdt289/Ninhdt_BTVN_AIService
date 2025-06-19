package com.example.ninhdt_btvn.ui.screen.main

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ninhdt_btvn.R
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.aisevice.data.remote.model.Config
import com.example.aisevice.data.remote.model.Domain
import com.example.aisevice.data.remote.model.StyleCategory
import com.example.aisevice.data.remote.model.StyleItem
import com.example.aisevice.data.remote.model.ThumbnailItem
import com.example.aisevice.data.remote.model.ThumbnailUrls
import com.example.ninhdt_btvn.utils.PermissionUtils
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.text.style.TextOverflow
import com.airbnb.lottie.compose.*
import com.example.ninhdt_btvn.ui.screen.main.component.GenerateButton
import com.example.ninhdt_btvn.ui.screen.main.component.LoadingDialog
import com.example.ninhdt_btvn.ui.screen.main.component.PhotoUploadArea
import com.example.ninhdt_btvn.ui.screen.main.component.PromptInputField
import com.example.ninhdt_btvn.ui.screen.main.component.StyleItemCard
import com.example.ninhdt_btvn.ui.screen.main.component.StyleList
import com.example.ninhdt_btvn.ui.screen.main.component.StyleSelectionPlaceholder
import com.example.ninhdt_btvn.ui.screen.main.component.StyleSelectionSection
import com.example.ninhdt_btvn.ui.screen.main.component.StyleTabsWithContent
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

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
                        viewModel.onEvent(MainUIEvent.SelectStyle(style.id))
                    }
                )
            } else {
                StyleSelectionPlaceholder()
            }
            GenerateButton(
                onClick = {
                    Log.d("GenerateButton", "Button clicked")
                    viewModel.onEvent(MainUIEvent.GenerateImage(imageUri, onImageSelected), context)
                },
                enabled = !state.isGenerating && state.selectedImage != null
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
            project = "test_project",
            name = "Style 1",
            key = "https://example.com/image1.jpg",
            config = Config(
                mode = 1,
                baseModel = "base_model_1",
                style = "style_1",
                positivePrompt = "good lighting",
                negativePrompt = "blurry",
                imageSize = "1024x1024",
                fixOpenpose = false,
                alpha = "0.5",
                strength = "0.8",
                guidanceScale = "7.5",
                numInferenceSteps = "50"
            ),
            mode = "auto",
            version = "v1",
            metadata = emptyList(),
            priority = 1.0,
            thumbnailApp = listOf(
                ThumbnailItem(
                    thumbnail = "https://example.com/thumb1.jpg",
                    thumbnailType = "cover",
                    id = "thumb1"
                )
            ),
            categories = listOf("art", "portrait"),
            segmentId = "segment_1",
            subscriptionType = "free",
            aiFamily = "ai_gen_1",
            styleType = "artistic",
            imageSize = "1024x1024",
            baseModel = "base_model_1",
            shouldCollectImage = false,
            versionCode = 1,
            createdAt = "2025-06-14T00:00:00Z",
            updatedAt = "2025-06-14T00:00:00Z",
            pricing = 0,
            thumbnail = ThumbnailUrls(
                before = "https://example.com/before1.jpg",
                after = "https://example.com/after1.jpg",
                key = "thumb_key1",
                previewStyle = "preview1",
                reminderAfter = null,
                reminderBefore = null,
                noti = null
            ),
            domain = Domain(
                id = "domain1",
                displayName = "Default Domain",
                name = "default",
                thumbnail = "https://example.com/domain_thumb.jpg",
                baseUrl = "https://domain.example.com",
                priority = 1.0,
                createdAt = "2025-01-01T00:00:00Z",
                updatedAt = "2025-01-02T00:00:00Z",
                versionCode = 1
            )
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
            project = "test_project",
            name = "Style 1",
            key = "https://example.com/image1.jpg",
            config = Config(
                mode = 1,
                baseModel = "base_model_1",
                style = "style_1",
                positivePrompt = "good lighting",
                negativePrompt = "blurry",
                imageSize = "1024x1024",
                fixOpenpose = false,
                alpha = "0.5",
                strength = "0.8",
                guidanceScale = "7.5",
                numInferenceSteps = "50"
            ),
            mode = "auto",
            version = "v1",
            metadata = emptyList(),
            priority = 1.0,
            thumbnailApp = listOf(
                ThumbnailItem(
                    thumbnail = "https://example.com/thumb1.jpg",
                    thumbnailType = "cover",
                    id = "thumb1"
                )
            ),
            categories = listOf("art", "portrait"),
            segmentId = "segment_1",
            subscriptionType = "free",
            aiFamily = "ai_gen_1",
            styleType = "artistic",
            imageSize = "1024x1024",
            baseModel = "base_model_1",
            shouldCollectImage = false,
            versionCode = 1,
            createdAt = "2025-06-14T00:00:00Z",
            updatedAt = "2025-06-14T00:00:00Z",
            pricing = 0,
            thumbnail = ThumbnailUrls(
                before = "https://example.com/before1.jpg",
                after = "https://example.com/after1.jpg",
                key = "thumb_key1",
                previewStyle = "preview1",
                reminderAfter = null,
                reminderBefore = null,
                noti = null
            ),
            domain = Domain(
                id = "domain1",
                displayName = "Default Domain",
                name = "default",
                thumbnail = "https://example.com/domain_thumb.jpg",
                baseUrl = "https://domain.example.com",
                priority = 1.0,
                createdAt = "2025-01-01T00:00:00Z",
                updatedAt = "2025-01-02T00:00:00Z",
                versionCode = 1
            )
        ),
        StyleItem(
            id = "2",
            project = "test_project",
            name = "Style 2",
            key = "https://example.com/image2.jpg",
            config = Config(
                mode = 2,
                baseModel = "base_model_2",
                style = "style_2",
                positivePrompt = "sharp focus",
                negativePrompt = "noise",
                imageSize = "512x512",
                fixOpenpose = true,
                alpha = "0.6",
                strength = "0.7",
                guidanceScale = "8.0",
                numInferenceSteps = "40"
            ),
            mode = "manual",
            version = "v2",
            metadata = emptyList(),
            priority = 2.0,
            thumbnailApp = listOf(
                ThumbnailItem(
                    thumbnail = "https://example.com/thumb2.jpg",
                    thumbnailType = "preview",
                    id = "thumb2"
                )
            ),
            categories = listOf("anime", "fantasy"),
            segmentId = "segment_2",
            subscriptionType = "premium",
            aiFamily = "ai_gen_2",
            styleType = "realistic",
            imageSize = "512x512",
            baseModel = "base_model_2",
            shouldCollectImage = true,
            versionCode = 2,
            createdAt = "2025-06-14T00:00:00Z",
            updatedAt = "2025-06-14T00:00:00Z",
            pricing = 1,
            thumbnail = ThumbnailUrls(
                before = "https://example.com/before2.jpg",
                after = "https://example.com/after2.jpg",
                key = "thumb_key2",
                previewStyle = "preview2",
                reminderAfter = "reminder_after_url",
                reminderBefore = "reminder_before_url",
                noti = "notification_url"
            ),
            domain = Domain(
                id = "domain2",
                displayName = "Premium Domain",
                name = "premium",
                thumbnail = "https://example.com/domain_thumb2.jpg",
                baseUrl = "https://premium.example.com",
                priority = 2.0,
                createdAt = "2025-01-03T00:00:00Z",
                updatedAt = "2025-01-04T00:00:00Z",
                versionCode = 2
            )
        )
    )

    StyleList(
        styles = sampleStyles,
        selectedStyle = sampleStyles[0],
        onStyleSelected = {}
    )
}

@Preview(showBackground = true)
@Composable
fun StyleTabsWithContentPreview() {
    val sampleCategories = listOf(
        com.example.aisevice.data.remote.model.StyleCategory(
            name = "Category 1",
            styles = listOf(
                StyleItem(
                    id = "1",
                    project = "test_project",
                    name = "Style 1",
                    key = "https://example.com/image1.jpg",
                    config = Config(
                        mode = 1,
                        baseModel = "base_model_1",
                        style = "style_1",
                        positivePrompt = "good lighting",
                        negativePrompt = "blurry",
                        imageSize = "1024x1024",
                        fixOpenpose = false,
                        alpha = "0.5",
                        strength = "0.8",
                        guidanceScale = "7.5",
                        numInferenceSteps = "50"
                    ),
                    mode = "auto",
                    version = "v1",
                    metadata = emptyList(),
                    priority = 1.0,
                    thumbnailApp = listOf(
                        ThumbnailItem(
                            thumbnail = "https://example.com/thumb1.jpg",
                            thumbnailType = "cover",
                            id = "thumb1"
                        )
                    ),
                    categories = listOf("art", "portrait"),
                    segmentId = "segment_1",
                    subscriptionType = "free",
                    aiFamily = "ai_gen_1",
                    styleType = "artistic",
                    imageSize = "1024x1024",
                    baseModel = "base_model_1",
                    shouldCollectImage = false,
                    versionCode = 1,
                    createdAt = "2025-06-14T00:00:00Z",
                    updatedAt = "2025-06-14T00:00:00Z",
                    pricing = 0,
                    thumbnail = ThumbnailUrls(
                        before = "https://example.com/before1.jpg",
                        after = "https://example.com/after1.jpg",
                        key = "thumb_key1",
                        previewStyle = "preview1",
                        reminderAfter = null,
                        reminderBefore = null,
                        noti = null
                    ),
                    domain = Domain(
                        id = "domain1",
                        displayName = "Default Domain",
                        name = "default",
                        thumbnail = "https://example.com/domain_thumb.jpg",
                        baseUrl = "https://domain.example.com",
                        priority = 1.0,
                        createdAt = "2025-01-01T00:00:00Z",
                        updatedAt = "2025-01-02T00:00:00Z",
                        versionCode = 1
                    )
                ),
                StyleItem(
                    id = "2",
                    project = "test_project",
                    name = "Style 2",
                    key = "https://example.com/image2.jpg",
                    config = Config(
                        mode = 2,
                        baseModel = "base_model_2",
                        style = "style_2",
                        positivePrompt = "sharp focus",
                        negativePrompt = "noise",
                        imageSize = "512x512",
                        fixOpenpose = true,
                        alpha = "0.6",
                        strength = "0.7",
                        guidanceScale = "8.0",
                        numInferenceSteps = "40"
                    ),
                    mode = "manual",
                    version = "v2",
                    metadata = emptyList(),
                    priority = 2.0,
                    thumbnailApp = listOf(
                        ThumbnailItem(
                            thumbnail = "https://example.com/thumb2.jpg",
                            thumbnailType = "preview",
                            id = "thumb2"
                        )
                    ),
                    categories = listOf("anime", "fantasy"),
                    segmentId = "segment_2",
                    subscriptionType = "premium",
                    aiFamily = "ai_gen_2",
                    styleType = "realistic",
                    imageSize = "512x512",
                    baseModel = "base_model_2",
                    shouldCollectImage = true,
                    versionCode = 2,
                    createdAt = "2025-06-14T00:00:00Z",
                    updatedAt = "2025-06-14T00:00:00Z",
                    pricing = 1,
                    thumbnail = ThumbnailUrls(
                        before = "https://example.com/before2.jpg",
                        after = "https://example.com/after2.jpg",
                        key = "thumb_key2",
                        previewStyle = "preview2",
                        reminderAfter = "reminder_after_url",
                        reminderBefore = "reminder_before_url",
                        noti = "notification_url"
                    ),
                    domain = Domain(
                        id = "domain2",
                        displayName = "Premium Domain",
                        name = "premium",
                        thumbnail = "https://example.com/domain_thumb2.jpg",
                        baseUrl = "https://premium.example.com",
                        priority = 2.0,
                        createdAt = "2025-01-03T00:00:00Z",
                        updatedAt = "2025-01-04T00:00:00Z",
                        versionCode = 2
                    )
                )
            ),
            id = "",
            priority = 3.14,
            project = "",
            segment = "",

            ),
        com.example.aisevice.data.remote.model.StyleCategory(
            name = "Category 2",
            styles = listOf(
                StyleItem(
                    id = "1",
                    project = "test_project",
                    name = "Style 1",
                    key = "https://example.com/image1.jpg",
                    config = Config(
                        mode = 1,
                        baseModel = "base_model_1",
                        style = "style_1",
                        positivePrompt = "good lighting",
                        negativePrompt = "blurry",
                        imageSize = "1024x1024",
                        fixOpenpose = false,
                        alpha = "0.5",
                        strength = "0.8",
                        guidanceScale = "7.5",
                        numInferenceSteps = "50"
                    ),
                    mode = "auto",
                    version = "v1",
                    metadata = emptyList(),
                    priority = 1.0,
                    thumbnailApp = listOf(
                        ThumbnailItem(
                            thumbnail = "https://example.com/thumb1.jpg",
                            thumbnailType = "cover",
                            id = "thumb1"
                        )
                    ),
                    categories = listOf("art", "portrait"),
                    segmentId = "segment_1",
                    subscriptionType = "free",
                    aiFamily = "ai_gen_1",
                    styleType = "artistic",
                    imageSize = "1024x1024",
                    baseModel = "base_model_1",
                    shouldCollectImage = false,
                    versionCode = 1,
                    createdAt = "2025-06-14T00:00:00Z",
                    updatedAt = "2025-06-14T00:00:00Z",
                    pricing = 0,
                    thumbnail = ThumbnailUrls(
                        before = "https://example.com/before1.jpg",
                        after = "https://example.com/after1.jpg",
                        key = "thumb_key1",
                        previewStyle = "preview1",
                        reminderAfter = null,
                        reminderBefore = null,
                        noti = null
                    ),
                    domain = Domain(
                        id = "domain1",
                        displayName = "Default Domain",
                        name = "default",
                        thumbnail = "https://example.com/domain_thumb.jpg",
                        baseUrl = "https://domain.example.com",
                        priority = 1.0,
                        createdAt = "2025-01-01T00:00:00Z",
                        updatedAt = "2025-01-02T00:00:00Z",
                        versionCode = 1
                    )
                ),
                StyleItem(
                    id = "2",
                    project = "test_project",
                    name = "Style 2",
                    key = "https://example.com/image2.jpg",
                    config = Config(
                        mode = 2,
                        baseModel = "base_model_2",
                        style = "style_2",
                        positivePrompt = "sharp focus",
                        negativePrompt = "noise",
                        imageSize = "512x512",
                        fixOpenpose = true,
                        alpha = "0.6",
                        strength = "0.7",
                        guidanceScale = "8.0",
                        numInferenceSteps = "40"
                    ),
                    mode = "manual",
                    version = "v2",
                    metadata = emptyList(),
                    priority = 2.0,
                    thumbnailApp = listOf(
                        ThumbnailItem(
                            thumbnail = "https://example.com/thumb2.jpg",
                            thumbnailType = "preview",
                            id = "thumb2"
                        )
                    ),
                    categories = listOf("anime", "fantasy"),
                    segmentId = "segment_2",
                    subscriptionType = "premium",
                    aiFamily = "ai_gen_2",
                    styleType = "realistic",
                    imageSize = "512x512",
                    baseModel = "base_model_2",
                    shouldCollectImage = true,
                    versionCode = 2,
                    createdAt = "2025-06-14T00:00:00Z",
                    updatedAt = "2025-06-14T00:00:00Z",
                    pricing = 1,
                    thumbnail = ThumbnailUrls(
                        before = "https://example.com/before2.jpg",
                        after = "https://example.com/after2.jpg",
                        key = "thumb_key2",
                        previewStyle = "preview2",
                        reminderAfter = "reminder_after_url",
                        reminderBefore = "reminder_before_url",
                        noti = "notification_url"
                    ),
                    domain = Domain(
                        id = "domain2",
                        displayName = "Premium Domain",
                        name = "premium",
                        thumbnail = "https://example.com/domain_thumb2.jpg",
                        baseUrl = "https://premium.example.com",
                        priority = 2.0,
                        createdAt = "2025-01-03T00:00:00Z",
                        updatedAt = "2025-01-04T00:00:00Z",
                        versionCode = 2
                    )
                )
            ),
            id = "",
            priority = 3.14,
            project = "",
            segment = "",

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
        com.example.aisevice.data.remote.model.StyleCategory(
            name = "Category 1",
            styles = listOf(
                StyleItem(
                    id = "1",
                    project = "test_project",
                    name = "Style 1",
                    key = "https://example.com/image1.jpg",
                    config = Config(
                        mode = 1,
                        baseModel = "base_model_1",
                        style = "style_1",
                        positivePrompt = "good lighting",
                        negativePrompt = "blurry",
                        imageSize = "1024x1024",
                        fixOpenpose = false,
                        alpha = "0.5",
                        strength = "0.8",
                        guidanceScale = "7.5",
                        numInferenceSteps = "50"
                    ),
                    mode = "auto",
                    version = "v1",
                    metadata = emptyList(),
                    priority = 1.0,
                    thumbnailApp = listOf(
                        ThumbnailItem(
                            thumbnail = "https://example.com/thumb1.jpg",
                            thumbnailType = "cover",
                            id = "thumb1"
                        )
                    ),
                    categories = listOf("art", "portrait"),
                    segmentId = "segment_1",
                    subscriptionType = "free",
                    aiFamily = "ai_gen_1",
                    styleType = "artistic",
                    imageSize = "1024x1024",
                    baseModel = "base_model_1",
                    shouldCollectImage = false,
                    versionCode = 1,
                    createdAt = "2025-06-14T00:00:00Z",
                    updatedAt = "2025-06-14T00:00:00Z",
                    pricing = 0,
                    thumbnail = ThumbnailUrls(
                        before = "https://example.com/before1.jpg",
                        after = "https://example.com/after1.jpg",
                        key = "thumb_key1",
                        previewStyle = "preview1",
                        reminderAfter = null,
                        reminderBefore = null,
                        noti = null
                    ),
                    domain = Domain(
                        id = "domain1",
                        displayName = "Default Domain",
                        name = "default",
                        thumbnail = "https://example.com/domain_thumb.jpg",
                        baseUrl = "https://domain.example.com",
                        priority = 1.0,
                        createdAt = "2025-01-01T00:00:00Z",
                        updatedAt = "2025-01-02T00:00:00Z",
                        versionCode = 1
                    )
                ),
                StyleItem(
                    id = "2",
                    project = "test_project",
                    name = "Style 2",
                    key = "https://example.com/image2.jpg",
                    config = Config(
                        mode = 2,
                        baseModel = "base_model_2",
                        style = "style_2",
                        positivePrompt = "sharp focus",
                        negativePrompt = "noise",
                        imageSize = "512x512",
                        fixOpenpose = true,
                        alpha = "0.6",
                        strength = "0.7",
                        guidanceScale = "8.0",
                        numInferenceSteps = "40"
                    ),
                    mode = "manual",
                    version = "v2",
                    metadata = emptyList(),
                    priority = 2.0,
                    thumbnailApp = listOf(
                        ThumbnailItem(
                            thumbnail = "https://example.com/thumb2.jpg",
                            thumbnailType = "preview",
                            id = "thumb2"
                        )
                    ),
                    categories = listOf("anime", "fantasy"),
                    segmentId = "segment_2",
                    subscriptionType = "premium",
                    aiFamily = "ai_gen_2",
                    styleType = "realistic",
                    imageSize = "512x512",
                    baseModel = "base_model_2",
                    shouldCollectImage = true,
                    versionCode = 2,
                    createdAt = "2025-06-14T00:00:00Z",
                    updatedAt = "2025-06-14T00:00:00Z",
                    pricing = 1,
                    thumbnail = ThumbnailUrls(
                        before = "https://example.com/before2.jpg",
                        after = "https://example.com/after2.jpg",
                        key = "thumb_key2",
                        previewStyle = "preview2",
                        reminderAfter = "reminder_after_url",
                        reminderBefore = "reminder_before_url",
                        noti = "notification_url"
                    ),
                    domain = Domain(
                        id = "domain2",
                        displayName = "Premium Domain",
                        name = "premium",
                        thumbnail = "https://example.com/domain_thumb2.jpg",
                        baseUrl = "https://premium.example.com",
                        priority = 2.0,
                        createdAt = "2025-01-03T00:00:00Z",
                        updatedAt = "2025-01-04T00:00:00Z",
                        versionCode = 2
                    )
                )
            ),

            id = "",
            priority = 3.14,
            project = "",
            segment = "",
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

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}