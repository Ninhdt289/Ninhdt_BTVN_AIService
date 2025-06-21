package com.example.ninhdt_btvn.ui.screen.pickphoto

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aisevice.data.local.model.DeviceImage

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.example.aisevice.data.local.repository.ImageRepository

class PickPhotoViewModel(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PickPhotoUiState())
    val uiState: StateFlow<PickPhotoUiState> = _uiState.asStateFlow()
    private val pageSize = 50
    private var currentPage = 0
    private var loadJob: kotlinx.coroutines.Job? = null

    init {
        _uiState.value = _uiState.value.copy(
        )
    }

    fun loadImages(loadMore: Boolean = false) {
        if (_uiState.value.isLoading || (loadMore && !_uiState.value.hasMoreImages)) return
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val offset = if (loadMore) _uiState.value.images.size else 0
                val images = imageRepository.getDeviceImages(offset, pageSize)

                val currentImages = if (loadMore) _uiState.value.images else emptyList()
                val newImages = currentImages + images
                val totalKnownCount = imageRepository.getTotalImageCount()
                val hasMore = newImages.size < totalKnownCount

                currentPage = newImages.size / pageSize

                _uiState.value = _uiState.value.copy(
                    images = newImages,
                    isLoading = false,
                    totalImages = totalKnownCount,
                    hasMoreImages = hasMore,
                    currentPage = currentPage
                )

            } catch (e: Exception) {
                Log.e("PickPhotoViewModel", "Error loading images", e)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun toggleImageSelection(imageId: Long) {
        val currentSelected = _uiState.value.selectedImage
        val newSelected = _uiState.value.images.find { it.id == imageId }
        _uiState.value = _uiState.value.copy(
            selectedImage = if (currentSelected?.id == imageId) null else newSelected
        )
    }

    fun setPermissionGranted(granted: Boolean) {
        _uiState.value = _uiState.value.copy(hasPermission = granted)
        if (granted && _uiState.value.images.isEmpty()) {
            loadImages()
        }
    }

}
