package com.example.ninhdt_btvn.ui.screen.main

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aisevice.data.remote.repository.StyleRepository
import com.example.ninhdt_btvn.data.repository.ImageUploadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: StyleRepository,
    private val imageUploadRepository: ImageUploadRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUIState())
    val uiState: StateFlow<MainUIState> = _uiState.asStateFlow()

    init {
        getListStyle()
    }

    private fun getListStyle() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }

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
            is MainUIEvent.NavigateToPickPhoto -> {
                // Handle navigation to photo picker
            }
            is MainUIEvent.SetSelectedImage -> {
                _uiState.update { it.copy(selectedImage = event.image) }
            }
            is MainUIEvent.SelectStyle -> {
                selectStyle(event.styleId)
            }
            MainUIEvent.ToggleStyleSelector -> {
                _uiState.update { it.copy(showStyleSelector = !it.showStyleSelector) }
            }
            is MainUIEvent.GenerateImage -> {
                Log.d("GenerateImage", "Generating image with prompt: ${_uiState.value.promptText}")
                generateImage(event.uri)
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

    private fun generateImage(uri: String?) {
        Log.d("GenerateImage", "uri: $uri")
        val selectedImage = uri
        if (selectedImage == null) {
            _uiState.update { it.copy(errorMessage = "Please select an image first") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, errorMessage = null) }
            
            imageUploadRepository.uploadImage(
                Uri.parse(selectedImage))
                .onSuccess { path ->
                    _uiState.update { 
                        it.copy(
                            isGenerating = false,
                            errorMessage = null
                        )
                    }
                    Log.d("Uploaded image path", path)
                    // TODO: Call API to generate image with the uploaded path
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


