package com.example.ninhdt_btvn.ui.screen.result

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.example.aisevice.data.local.repository.ImageRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResultViewModel(private val imageRepository: ImageRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    fun updateImageUrl(url: String?) {
        _uiState.update {
            it.copy(imageUrl = url)
        }
    }

    fun downloadImage() {
        val imageUrl = _uiState.value.imageUrl
        if (imageUrl.isNullOrEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "No image to download.")
            }
            return
        }

        _uiState.update {
            it.copy(isDownloading = true, errorMessage = null)
        }

        viewModelScope.launch {
            try {
                val timeStamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "IMG_AI_$timeStamp.jpg"
                val uri = imageRepository.downloadImage(imageUrl, fileName)

                if (uri != null) {
                    _uiState.update {
                        it.copy(isDownloading = false, errorMessage = null)
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isDownloading = false,
                            errorMessage = "Download failed."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        errorMessage = "Download failed: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
}