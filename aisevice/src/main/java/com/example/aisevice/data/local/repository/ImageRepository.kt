package com.example.aisevice.data.local.repository

import com.example.aisevice.data.local.model.DeviceImage
import android.net.Uri

interface ImageRepository {
    suspend fun getDeviceImages(offset: Int = 0, limit: Int = 50): List<DeviceImage>
    suspend fun getTotalImageCount(): Int
    suspend fun downloadImage(imageUrl: String, fileName: String): Uri?
}