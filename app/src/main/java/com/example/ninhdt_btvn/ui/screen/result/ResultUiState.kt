package com.example.ninhdt_btvn.ui.screen.result

data class ResultUiState(
    val imageUrl: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isDownloading: Boolean = false
)