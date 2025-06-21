package com.example.ninhdt_btvn.ui.screen.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aisevice.data.local.impl.ImageRepositoryImpl
import com.example.aisevice.data.remote.repository.StyleRepository
import com.example.ninhdt_btvn.data.repository.ImageUploadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.aisevice.data.remote.request.AiArtRequest
import com.example.aisevice.data.local.repository.ImageRepository
import com.example.aisevice.data.remote.model.StyleItem

class MainViewModel(
    private val repository: StyleRepository,
    private val imageUploadRepository: ImageUploadRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUIState())
    val uiState: StateFlow<MainUIState> = _uiState.asStateFlow()

    fun startImagePreloading() {
        viewModelScope.launch {
            (imageRepository as? ImageRepositoryImpl)?.preloadInitialPages()
        }
    }

    private fun getListStyle() {
        viewModelScope.launch {
            val result = repository.getStyles()

            result
                .onSuccess { response ->
                    val styles = response.data.items
                    _uiState.update {
                        it.copy(
                            availableStyles = styles,
                            isGenerating = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            errorMessage = e.message ?: "Unknown error"
                        )
                    }
                }
        }
    }

    fun onEvent(event: MainUIEvent) {
        when (event) {
            is MainUIEvent.UpdatePromptText -> {
                _uiState.update { it.copy(promptText = event.text) }
            }

            MainUIEvent.ClearError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }

            is MainUIEvent.SelectStyle -> {
                selectStyle(event.style)
            }

            is MainUIEvent.GenerateImage -> {
                generateImage(event.uri, event.onImageGenerated)
            }


            is MainUIEvent.ReloadStyles -> {
                getListStyle()
            }

            is MainUIEvent.SetError -> {
                _uiState.update { it.copy(errorMessage = event.errorMessage) }
            }
        }
    }

    private fun selectStyle(style: StyleItem) {
        _uiState.update { state ->
            state.copy(
                selectedStyle = style,
            )
        }
    }

    private fun generateImage(uri: String?, onImageGenerated: (String) -> Unit) {
        val selectedImage = uri
        val styleId = _uiState.value.selectedStyle?.id
        val prompt =
            _uiState.value.promptText.ifBlank { _uiState.value.selectedStyle?.config?.positivePrompt }


        if (selectedImage == null) {
            _uiState.update { it.copy(errorMessage = "Please select an image first") }
            return
        }
        if (styleId == null) {
            _uiState.update { it.copy(errorMessage = "Please select a style first") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, errorMessage = null) }

            imageUploadRepository.uploadImage(Uri.parse(selectedImage))
                .onSuccess { uploadedPath ->
                    val request = AiArtRequest(
                        file = uploadedPath,
                        styleId = styleId,
                        positivePrompt = prompt,
                        negativePrompt = _uiState.value.selectedStyle?.config?.negativePrompt,
                    )

                    imageUploadRepository.generateArt(request)
                        .onSuccess { response ->
                            val imageUrl = response.body()?.data?.url ?: ""
                            _uiState.update {
                                it.copy(
                                    isGenerating = false,
                                    errorMessage = null,
                                    imageUrl = imageUrl
                                )
                            }
                            onImageGenerated(imageUrl)
                        }
                        .onFailure { error ->
                            _uiState.update {
                                it.copy(
                                    isGenerating = false,
                                    errorMessage = error.message ?: "Failed to generate image"
                                )
                            }
                        }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            errorMessage = error.message ?: "Failed to upload image"
                        )
                    }
                }
        }
    }
}


