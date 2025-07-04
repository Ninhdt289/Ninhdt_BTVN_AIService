package com.example.ninhdt_btvn.ui.screen.pickphoto

import android.annotation.SuppressLint
import android.net.Uri
import com.example.aisevice.data.local.impl.ImageRepositoryImpl
import com.example.ninhdt_btvn.ui.screen.pickphoto.component.DeviceImageGallery
import com.example.ninhdt_btvn.ui.screen.pickphoto.component.TopBar
import com.example.ninhdt_btvn.utils.PermissionUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.aisevice.data.local.model.DeviceImage
import androidx.compose.ui.tooling.preview.Preview
import com.example.ninhdt_btvn.R
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun PickPhotoScreen(
    onClose: () -> Unit = {},
    onNext: () -> Unit = {},
    onImageSelected: (DeviceImage) -> Unit = {},
    viewModel: PickPhotoViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val lazyGridState = rememberLazyGridState()

    val permissionLauncher = PermissionUtils.rememberPermissionLauncher(
        onPermissionGranted = {},
        onPermissionDenied = { }
    )

    LaunchedEffect(Unit) {
        val hasPermission = PermissionUtils.hasImagePermissions(context)
        if (hasPermission) {
            viewModel.setPermissionGranted(true)
        } else {
            permissionLauncher.launch(PermissionUtils.getRequiredPermissions())
        }
    }

    LaunchedEffect(lazyGridState) {
        snapshotFlow { lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastIndex ->
                if (lastIndex != null && lastIndex >= uiState.images.size - 5) {
                    viewModel.loadImages(loadMore = true)
                }
            }
    }

    Scaffold(
        topBar = {
            TopBar(
                onClose = onClose,
                onNext = {
                    uiState.selectedImage?.let { selectedImage ->
                        onImageSelected(selectedImage)
                        onNext()
                    }
                },
                nextEnabled = uiState.selectedImage != null
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        if (!uiState.hasPermission) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.pickphoto_permission),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = {
                        permissionLauncher.launch(PermissionUtils.getRequiredPermissions())
                    }
                ) {
                    Text(text = stringResource(R.string.pickphoto_go_to_setting))
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                DeviceImageGallery(
                    images = uiState.images,
                    selectedImageId = uiState.selectedImage?.id,
                    onToggleImage = viewModel::toggleImageSelection,
                    isLoading = uiState.isLoading,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    lazyGridState = lazyGridState
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    TopBar(
        onClose = {},
        onNext = {},
        nextEnabled = true
    )
}

@Preview(showBackground = true)
@Composable
fun DeviceImageGalleryPreview() {
    val sampleImages = listOf(
        DeviceImage(
            id = 1,
            uri = Uri.parse("content://media/external/images/media/1"),
            size = 1024,
            dateAdded = System.currentTimeMillis(),
            mimeType = "",
            displayName = ""
        ),
        DeviceImage(
            id = 2,
            uri = Uri.parse("content://media/external/images/media/1"),
            size = 1024,
            dateAdded = System.currentTimeMillis(),
            mimeType = "",
            displayName = ""
        )
    )

    DeviceImageGallery(
        images = sampleImages,
        selectedImageId = 1,
        onToggleImage = {},
        isLoading = false,
        modifier = Modifier.fillMaxSize()
    )
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun PickPhotoScreenPreview() {
    val mockViewModel = PickPhotoViewModel(
        imageRepository = ImageRepositoryImpl(LocalContext.current.contentResolver)
    )
    PickPhotoScreen(
        onClose = {},
        onNext = {},
        onImageSelected = {},
        viewModel = mockViewModel
    )
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun PickPhotoScreenWithPermissionDeniedPreview() {
    val mockViewModel = PickPhotoViewModel(
        imageRepository = ImageRepositoryImpl(LocalContext.current.contentResolver)
    )
    PickPhotoScreen(
        onClose = {},
        onNext = {},
        onImageSelected = {},
        viewModel = mockViewModel
    )
}
