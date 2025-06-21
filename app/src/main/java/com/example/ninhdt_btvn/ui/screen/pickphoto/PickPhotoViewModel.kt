package com.example.ninhdt_btvn.ui.screen.pickphoto

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aisevice.data.local.model.DeviceImage
import com.example.ninhdt_btvn.ui.shared.SharedState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.aisevice.data.local.repository.ImageRepository

class PickPhotoViewModel(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PickPhotoUiState())
    val uiState: StateFlow<PickPhotoUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            images = SharedState.images,
            hasPermission = SharedState.hasPermission,
            currentPage = SharedState.currentPage,
            totalImages = SharedState.totalImages,
            hasMoreImages = SharedState.hasMoreImages
        )
    }

    fun loadImages(loadMore: Boolean = false) {
        if (_uiState.value.isLoading || (!loadMore && !_uiState.value.hasMoreImages)) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val offset = if (loadMore) {
                    SharedState.lastLoadedOffset
                } else {
                    0
                }

                val images = imageRepository.getDeviceImages(offset, SharedState.lastLoadedLimit)
                val totalImages = imageRepository.getTotalImageCount()

                val newImages = if (loadMore) SharedState.images + images else images
                val hasMore = (offset + images.size) < totalImages

                _uiState.value = _uiState.value.copy(
                    images = newImages,
                    isLoading = false,
                    currentPage = offset / SharedState.lastLoadedLimit,
                    totalImages = totalImages,
                    hasMoreImages = hasMore
                )

                SharedState.apply {
                    this.images = newImages
                    this.isLoading = false
                    this.currentPage = _uiState.value.currentPage
                    this.totalImages = totalImages
                    this.hasMoreImages = hasMore
                    this.lastLoadedOffset = offset + lastLoadedLimit
                }

                if (!loadMore && hasMore && images.isNotEmpty()) {
                    kotlinx.coroutines.delay(150)
                    loadImages(loadMore = true)
                }
            } catch (e: Exception) {
                Log.e("PickPhotoViewModel", "Error loading images", e)
                _uiState.value = _uiState.value.copy(isLoading = false)
                SharedState.isLoading = false
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
        SharedState.hasPermission = granted
        _uiState.value = _uiState.value.copy(hasPermission = granted)
        if (granted && SharedState.lastLoadedOffset == 0) {
            loadImages()
        }
    }

}
