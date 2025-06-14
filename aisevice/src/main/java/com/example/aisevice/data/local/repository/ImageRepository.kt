package com.example.aisevice.data.local.repository

import com.example.aisevice.data.local.model.DeviceImage

interface ImageRepository {
    suspend fun getDeviceImages(): List<DeviceImage>
}