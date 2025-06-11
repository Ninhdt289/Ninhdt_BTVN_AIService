package com.example.ninhdt_btvn.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ninhdt_btvn.data.remote.repository.StyleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val repository = StyleRepository()
    private val _uiState = MutableStateFlow(MainUIState())
    val uiState: StateFlow<MainUIState> = _uiState

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
                            errorMessage = e.message
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
            MainUIEvent.NavigateToPickPhoto -> {
            }
            is MainUIEvent.SetSelectedImage -> {
                _uiState.update { it.copy(selectedImageId = event.imageId) }
            }
            else -> {}
        }
    }
}