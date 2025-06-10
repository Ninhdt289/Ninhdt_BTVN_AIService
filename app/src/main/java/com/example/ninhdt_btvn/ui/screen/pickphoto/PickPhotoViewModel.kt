package com.example.ninhdt_btvn.ui.screen.pickphoto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.ninhdt_btvn.data.local.repository.ImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class PickPhotoViewModel(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PickPhotoUiState())
    val uiState: StateFlow<PickPhotoUiState> = _uiState.asStateFlow()

    fun loadImages() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val images = imageRepository.getDeviceImages()
                _uiState.value = _uiState.value.copy(
                    images = images,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun toggleImageSelection(imageId: Long) {
        val currentSelected = _uiState.value.selectedImages
        val newSelected = if (currentSelected.contains(imageId)) {
            currentSelected - imageId
        } else {
            currentSelected + imageId
        }
        _uiState.value = _uiState.value.copy(selectedImages = newSelected)
    }

    fun setPermissionGranted(granted: Boolean) {
        _uiState.value = _uiState.value.copy(hasPermission = granted)
        if (granted) {
            loadImages()
        }
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedImages = emptySet())
    }
}
