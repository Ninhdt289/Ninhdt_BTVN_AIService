package com.example.ninhdt_btvn.ui.screen.pickphoto

import com.example.aisevice.data.local.model.DeviceImage

data class PickPhotoUiState(
    val selectedImage: DeviceImage? = null,
    val hasPermission: Boolean = false
)