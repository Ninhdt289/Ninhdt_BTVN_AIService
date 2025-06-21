package com.example.ninhdt_btvn.ui.shared

import com.example.aisevice.data.local.model.DeviceImage

object SharedState {
    var images: List<DeviceImage> = emptyList()
    var isLoading: Boolean = false
    var hasPermission: Boolean = false
    var currentPage: Int = 0
    var totalImages: Int = 0
    var hasMoreImages: Boolean = true

    var lastLoadedOffset: Int = 0
    var lastLoadedLimit: Int = 20
} 