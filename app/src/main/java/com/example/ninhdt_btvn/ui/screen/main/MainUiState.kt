package com.example.ninhdt_btvn.ui.screen.main

import androidx.compose.runtime.Stable
import android.graphics.Bitmap
import com.example.ninhdt_btvn.data.remote.model.StyleCategory
import com.example.ninhdt_btvn.data.remote.model.StyleResponse

@Stable
data class MainUIState(
    val promptText: String = "",

    val generatedImage: Bitmap? = null,

    val availableStyles: List<StyleCategory>? = null,

    val selectedStyleId: String? = null,

    val isGenerating: Boolean = false,

    val errorMessage: String? = null,

    val showStyleSelector: Boolean = false
)
sealed class MainUIEvent {
    data class UpdatePromptText(val text: String) : MainUIEvent()
    data class SelectStyle(val styleId: String) : MainUIEvent()
    object GenerateImage : MainUIEvent()
    object ClearError : MainUIEvent()
    object ToggleStyleSelector : MainUIEvent()
    object ClearGeneratedImage : MainUIEvent()
    object NavigateToPickPhoto : MainUIEvent()
}