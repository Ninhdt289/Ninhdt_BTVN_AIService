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
import kotlinx.coroutines.flow.update

@Composable
fun MainScreen(
    imageUri: String? = null,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel(),
    onGenerate: () -> Unit = {},
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val permissionLauncher = PermissionUtils.rememberPermissionLauncher(
        onPermissionGranted = onGenerate,
        onPermissionDenied = { }
    )

    LaunchedEffect(Unit) {
        Log.d("MainScreen", "LaunchedEffect triggered ${imageUri}")
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
            onChangeImage = {  viewModel.onEvent(MainUIEvent.NavigateToPickPhoto)
                if (PermissionUtils.hasImagePermissions(context)) {
                    onGenerate()
                } else {
                    permissionLauncher.launch(PermissionUtils.getRequiredPermissions())
                }},
            selectedImage = imageUri
        )

        when {
            state.isGenerating -> Text("Loading styles...")

            state.errorMessage != null -> Text("Error: ${state.errorMessage}")

            !state.availableStyles.isNullOrEmpty() -> {
                StyleSelectionSection(
                    styleList = state.availableStyles,
                    selectedStyle = state.selectedStyle,
                    onStyleSelected = { style ->
                        viewModel._uiState.update { it.copy(selectedStyle = style) }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        GenerateButton(
            onClick = {

            }
        )
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
    onChangeImage: () -> Unit,
    selectedImage: String? = null
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
            .clickable { onChangeImage() },
        contentAlignment = Alignment.Center
    ) {
        if (selectedImage != null) {
            AsyncImage(
                model = selectedImage,
                contentDescription = "Selected image",
                modifier = Modifier.fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )
            
            Image(
                painter = painterResource(id = R.drawable.ic_main_change_image),
                contentDescription = "Change image",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(50.dp)
                    .clickable { onChangeImage() }
                    .padding(top = 15.dp, start = 19.dp),
                contentScale = ContentScale.Fit
            )
        } else {
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
        }
    }
}

@Composable
fun StyleItemCard(
    styleItem: com.example.aisevice.data.remote.model.StyleItem,
    isSelected: Boolean = false,
    onSelect: (com.example.aisevice.data.remote.model.StyleItem) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = Color(0xFFE400D9),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { onSelect(styleItem) }
        ) {
            UrlImageWithCoil(styleItem.key)
        }

        Text(
            text = styleItem.name,
            fontSize = 12.sp,
            color = if (isSelected) Color(0xFFE400D9) else Color.Black
        )
    }
}

@Composable
fun StyleList(
    styles: List<StyleItem>,
    selectedStyle: StyleItem?,
    onStyleSelected: (StyleItem) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(styles) { styleItem ->
            StyleItemCard(
                styleItem = styleItem,
                isSelected = styleItem == selectedStyle,
                onSelect = onStyleSelected
            )
        }
    }
}

@Composable
fun StyleTabsWithContent(
    styleList: List<StyleCategory>,
    selectedStyle: StyleItem?,
    onStyleSelected: (StyleItem) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = Color(0xFFE400D9),
            edgePadding = 2.dp,
            indicator = { tabPositions ->
                val currentTab = tabPositions[selectedTabIndex]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.BottomStart)
                        .offset(x = currentTab.left + (currentTab.width - 16.dp) / 2)
                        .width(16.dp)
                        .height(2.dp)
                        .background(Color(0xFFE400D9), shape = CircleShape)
                )
            },
            divider = {}
        ) {
            styleList.forEachIndexed { index, category ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = {
                        Text(
                            text = category.name,
                            fontSize = 14.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) Color(0xFFE400D9) else Color.Gray,
                            maxLines = 1,
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        StyleList(
            styles = styleList[selectedTabIndex].styles,
            selectedStyle = selectedStyle,
            onStyleSelected = onStyleSelected
        )
    }
}

@Composable
fun StyleSelectionSection(
    styleList: List<StyleCategory>?,
    selectedStyle: StyleItem?,
    onStyleSelected: (StyleItem) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.main_style_title),
            color = Color(0xFFE400D9),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        if (!styleList.isNullOrEmpty()) {
            StyleTabsWithContent(
                styleList = styleList,
                selectedStyle = selectedStyle,
                onStyleSelected = onStyleSelected
            )
        }
    }
}

@Composable
fun UrlImageWithCoil(url: String) {
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun GenerateButton(
    onClick: () -> Unit
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

/*data class StyleItem(
    val name: String,
    val imageResource: String
)*/

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
        styleItem =  StyleItem(
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
    ),  id = "",
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
            ),  id = "",
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