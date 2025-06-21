package com.example.ninhdt_btvn.ui.screen.pickphoto

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.aisevice.data.local.impl.DeviceImagePagingSource
import com.example.aisevice.data.local.model.DeviceImage
import com.example.aisevice.data.local.repository.ImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PickPhotoViewModel(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PickPhotoUiState())
    val uiState: StateFlow<PickPhotoUiState> = _uiState.asStateFlow()

    // Paging 3 configuration
    private val pagingConfig = PagingConfig(
        pageSize = 50,
        enablePlaceholders = false,
        prefetchDistance = 2
    )

    val pagingData: kotlinx.coroutines.flow.Flow<PagingData<DeviceImage>> = Pager(
        config = pagingConfig,
        pagingSourceFactory = { DeviceImagePagingSource(imageRepository) }
    ).flow.cachedIn(viewModelScope)

/*
    fun toggleImageSelection(imageId: Long) {
        val currentSelected = _uiState.value.selectedImage
        val newSelected = _uiState.value.images.find { it.id == imageId }
        _uiState.value = _uiState.value.copy(
            selectedImage = if (currentSelected?.id == imageId) null else newSelected
        )
    }
*/

    fun setPermissionGranted(granted: Boolean) {
        _uiState.value = _uiState.value.copy(hasPermission = granted)
    }

    fun updateSelectedImage(image: DeviceImage?) {
        _uiState.value = _uiState.value.copy(selectedImage = image)
    }
}
