package com.example.ninhdt_btvn.ui.screen.pickphoto

import android.util.Log
import com.example.ninhdt_btvn.data.local.repository.ImageRepository
import com.example.ninhdt_btvn.ui.screen.pickphoto.component.DeviceImageGallery
import com.example.ninhdt_btvn.ui.screen.pickphoto.component.TopBar
import com.example.ninhdt_btvn.utils.PermissionUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun PickPhotoScreen() {
    val context = LocalContext.current
    val imageRepository = remember { ImageRepository(context.contentResolver) }
    val viewModel: PickPhotoViewModel = viewModel { PickPhotoViewModel(imageRepository) }

    val uiState by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        viewModel.setPermissionGranted(allGranted)
    }

    LaunchedEffect(Unit) {
        val hasPermission = PermissionUtils.hasImagePermissions(context)
        if (hasPermission) {
            viewModel.setPermissionGranted(true)
        } else {
            permissionLauncher.launch(PermissionUtils.getRequiredPermissions())
        }
    }

    val handleClose = {
        println("Close gallery")
    }

    val handleNext = {
        val selectedImageIds = uiState.selectedImages
        val selectedImages = uiState.images.filter { selectedImageIds.contains(it.id) }
        println("Selected ${selectedImages.size} images")
    }

    Scaffold(
        topBar = {
            TopBar(
                onClose = handleClose,
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
                    text = "Cần quyền truy cập để xem ảnh trong thiết bị",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = {
                        permissionLauncher.launch(PermissionUtils.getRequiredPermissions())
                    }
                ) {
                    Text("Cấp quyền")
                }
            }
        } else {
            DeviceImageGallery(
                images = uiState.images,
                selectedImages = uiState.selectedImages,
                onToggleImage = viewModel::toggleImageSelection,
                isLoading = uiState.isLoading,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
}
