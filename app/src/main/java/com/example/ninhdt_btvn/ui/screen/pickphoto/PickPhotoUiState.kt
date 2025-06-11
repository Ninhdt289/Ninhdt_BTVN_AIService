package com.example.ninhdt_btvn.ui.screen.pickphoto

import com.example.ninhdt_btvn.data.local.model.DeviceImage

data class PickPhotoUiState(
    val images: List<DeviceImage> = emptyList(),
    val selectedImage: DeviceImage? = null,
    val isLoading: Boolean = false,
    val hasPermission: Boolean = false
)