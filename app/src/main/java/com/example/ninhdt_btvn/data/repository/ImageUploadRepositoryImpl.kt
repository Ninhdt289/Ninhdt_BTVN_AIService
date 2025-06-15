package com.example.ninhdt_btvn.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.aisevice.data.client.ApiClient
import com.example.ninhdt_btvn.data.api.AIServiceApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class ImageUploadRepositoryImpl(
    private val context: Context
): ImageUploadRepository {
    override suspend fun uploadImage(imageUri: Uri): Result<String> {
        Log.d("ImageUploadRepositoryImpl", "uploadImage: $imageUri")
        return try {
            val presignedUrlResponse = ApiClient.genApi.getPresignedUrl()
            Log.d("ImageUploadRepositoryImpl", "uploadImage: $presignedUrlResponse")
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            FileOutputStream(tempFile).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            
            val requestBody = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val response = okhttp3.OkHttpClient().newCall(
                okhttp3.Request.Builder()
                    .url(presignedUrlResponse.data.url)
                    .put(requestBody)
                    .build()
            ).execute()
            
            if (response.isSuccessful) {
                Log.d("ImageUploadRepositoryImpl", "uploadImage: ${presignedUrlResponse.data.path}")
                Result.success(presignedUrlResponse.data.path)

            } else {
                Result.failure(Exception("Failed to upload image: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 