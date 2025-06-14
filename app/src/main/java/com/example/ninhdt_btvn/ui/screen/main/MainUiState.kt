package com.example.ninhdt_btvn.ui.screen.main

import android.graphics.Bitmap
import androidx.compose.runtime.Stable
import com.example.aisevice.data.remote.model.StyleCategory
import com.example.aisevice.data.remote.model.StyleItem
import com.example.ninhdt_btvn.data.local.model.DeviceImage

@Stable
data class MainUIState(
    val promptText: String = "",

    val generatedImage: Bitmap? = null,

    val availableStyles: List<StyleCategory>? = null,

    val selectedStyle: StyleItem? = null,

    val selectedStyleId: String? = null,

    val isGenerating: Boolean = false,

    val errorMessage: String? = null,

    val showStyleSelector: Boolean = false,

    val selectedImage: DeviceImage? = null
)
sealed class MainUIEvent {
    data class UpdatePromptText(val text: String) : MainUIEvent()
    data class SelectStyle(val styleId: String) : MainUIEvent()
    object GenerateImage : MainUIEvent()
    object ClearError : MainUIEvent()
    object ToggleStyleSelector : MainUIEvent()
    object ClearGeneratedImage : MainUIEvent()
    object NavigateToPickPhoto : MainUIEvent()
    data class SetSelectedImage(val image: DeviceImage) : MainUIEvent()
}