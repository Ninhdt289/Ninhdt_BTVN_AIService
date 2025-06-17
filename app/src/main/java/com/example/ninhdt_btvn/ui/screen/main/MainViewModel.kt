package com.example.ninhdt_btvn.ui.screen.main

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aisevice.data.remote.repository.StyleRepository
import com.example.ninhdt_btvn.data.repository.ImageUploadRepository
import com.example.ninhdt_btvn.ui.shared.SharedState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.aisevice.data.remote.request.AiArtRequest
import com.example.aisevice.data.local.repository.ImageRepository

class MainViewModel(
    private val repository: StyleRepository,
    private val imageUploadRepository: ImageUploadRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUIState())
    val uiState: StateFlow<MainUIState> = _uiState.asStateFlow()

    private val pageSize = 50

    init {
        getListStyle()
        loadImages()
    }

    private fun loadImages() {
        if (SharedState.isLoading || (!SharedState.hasMoreImages)) return

        viewModelScope.launch {
            SharedState.isLoading = true
            try {
                val currentPage = SharedState.currentPage
                val offset = currentPage * pageSize
                
                val images = imageRepository.getDeviceImages(offset, pageSize)
                val totalImages = imageRepository.getTotalImageCount()
                
                SharedState.apply {
                    this.images = if (currentPage == 0) images else this.images + images
                    this.isLoading = false
                    this.currentPage = currentPage
                    this.totalImages = totalImages
                    this.hasMoreImages = (offset + images.size) < totalImages
                }
            } catch (e: Exception) {
                SharedState.isLoading = false
            }
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

    fun onEvent(event: MainUIEvent, context: Context? = null) {
        when (event) {
            is MainUIEvent.UpdatePromptText -> {
                _uiState.update { it.copy(promptText = event.text) }
            }
            MainUIEvent.ClearError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
            is MainUIEvent.NavigateToPickPhoto -> {
                // No need to load images here since we load them in init
            }
            is MainUIEvent.SetSelectedImage -> {
                _uiState.update { it.copy(imageUrl = event.image) }
            }
            is MainUIEvent.SelectStyle -> {
                selectStyle(event.styleId)
            }
            MainUIEvent.ToggleStyleSelector -> {
                _uiState.update { it.copy(showStyleSelector = !it.showStyleSelector) }
            }
            is MainUIEvent.GenerateImage -> {
                Log.d("GenerateImage", "Generating image with prompt: ${_uiState.value.promptText}")
                generateImage(context!!, event.uri, event.onImageGenerated)
            }
            MainUIEvent.ClearGeneratedImage -> {
                _uiState.update { it.copy(generatedImage = null) }
            }
        }
    }

    private fun selectStyle(styleId: String) {
        _uiState.update { state ->
            val selectedStyle = state.availableStyles?.flatMap { it.styles }?.find { it.id == styleId }
            state.copy(
                selectedStyle = selectedStyle,
                selectedStyleId = styleId
            )
        }
    }

    private fun generateImage(context: Context, uri: String?, onImageGenerated: (String) -> Unit) {
        Log.d("GenerateImage", "Bắt đầu generateImage với uri: $uri")
        val selectedImage = uri
        val styleId = _uiState.value.selectedStyleId ?: _uiState.value.selectedStyle?.id
        val prompt = _uiState.value.promptText

        if (selectedImage == null) {
            Log.d("GenerateImage", "Chưa chọn ảnh")
            _uiState.update { it.copy(errorMessage = "Please select an image first") }
            return
        }
        if (styleId == null) {
            Log.d("GenerateImage", "Chưa chọn style")
            _uiState.update { it.copy(errorMessage = "Please select a style first") }
            return
        }

        viewModelScope.launch {
            Log.d("GenerateImage", "Bắt đầu upload ảnh lên cloud...")
            _uiState.update { it.copy(isGenerating = true, errorMessage = null) }

            imageUploadRepository.uploadImage(Uri.parse(selectedImage))
                .onSuccess { uploadedPath ->
                    Log.d("GenerateImage", "Upload thành công. Path trên cloud: $uploadedPath. Bắt đầu gen AI art...")

                    val request = AiArtRequest(
                        file = uploadedPath,
                        styleId = styleId,
                        positivePrompt = prompt,
                        negativePrompt = null,
                        imageSize = null
                    )

                    imageUploadRepository.generateArt(request)
                        .onSuccess { response ->
                            Log.d("GenerateImage", "Gen AI art thành công: ${response.body()?.data?.url}")
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
                            Log.d("GenerateImage", "Gen AI art thất bại: ${error.message}")
                            _uiState.update {
                                it.copy(
                                    isGenerating = false,
                                    errorMessage = error.message ?: "Failed to generate image"
                                )
                            }
                        }
                }
                .onFailure { error ->
                    Log.d("GenerateImage", "Upload ảnh thất bại: ${error.message}")
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


