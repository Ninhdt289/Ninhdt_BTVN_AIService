package com.example.ninhdt_btvn.ui.screen.pickphoto

import com.example.aisevice.data.local.model.DeviceImage

data class PickPhotoUiState(
    val images: List<DeviceImage> = emptyList(),
    val selectedImage: DeviceImage? = null,
    val isLoading: Boolean = false,
    val hasPermission: Boolean = false,
    val currentPage: Int = 0,
    val totalImages: Int = 0,
    val hasMoreImages: Boolean = true
)