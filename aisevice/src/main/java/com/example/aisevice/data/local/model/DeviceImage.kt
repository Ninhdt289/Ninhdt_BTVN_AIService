package com.example.aisevice.data.local.model

import android.graphics.Bitmap
import android.net.Uri

data class DeviceImage(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val dateAdded: Long,
    val size: Long,
    val mimeType: String,
    val bitmap: Bitmap? = null
)
