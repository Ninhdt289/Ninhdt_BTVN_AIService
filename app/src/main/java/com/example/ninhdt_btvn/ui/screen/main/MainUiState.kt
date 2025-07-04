package com.example.ninhdt_btvn.ui.screen.main

import android.graphics.Bitmap
import androidx.compose.runtime.Stable
import com.example.aisevice.data.remote.model.StyleCategory
import com.example.aisevice.data.remote.model.StyleItem
import com.example.aisevice.data.local.model.DeviceImage

@Stable
data class MainUIState(
    val promptText: String = "",

    val availableStyles: List<StyleCategory>? = null,

    val selectedStyle: StyleItem? = null,

    val isGenerating: Boolean = false,

    val errorMessage: String? = null,

    val selectedImage: DeviceImage? = null,

    val imageUrl: String? = null
)

sealed class MainUIEvent {
    data class UpdatePromptText(val text: String) : MainUIEvent()
    data class SetError(val errorMessage: String) : MainUIEvent()
    data class SelectStyle(val style: StyleItem) : MainUIEvent()
    data class GenerateImage(
        val uri: String?,
        val onImageGenerated: (String) -> Unit
    ) : MainUIEvent()
    data object ReloadStyles : MainUIEvent()
    data object ClearError : MainUIEvent()

}