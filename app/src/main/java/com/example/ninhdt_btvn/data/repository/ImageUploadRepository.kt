package com.example.ninhdt_btvn.data.repository

import android.net.Uri

interface ImageUploadRepository {
    suspend fun uploadImage(imageUri: Uri): Result<String>
}