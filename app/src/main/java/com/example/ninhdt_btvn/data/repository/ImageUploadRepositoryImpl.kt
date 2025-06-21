package com.example.ninhdt_btvn.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.aisevice.data.client.ApiClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import com.example.aisevice.data.remote.request.AiArtRequest
import com.example.aisevice.data.remote.request.AiArtResponse
import com.example.aisevice.data.remote.request.AIServiceApi

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ImageUploadRepositoryImpl(
    private val context: Context
) : ImageUploadRepository, KoinComponent {
    override suspend fun uploadImage(imageUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val presignedUrlResponse = ApiClient.genApi.getPresignedUrl()
            clearTempUploadedImages()
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) {
                return@withContext Result.failure(Exception("Failed to decode image"))
            }

            val maxDimension = 1024
            val (width, height) = originalBitmap.width to originalBitmap.height
            val scale = if (width > maxDimension || height > maxDimension) {
                val scaleFactor = maxDimension.toFloat() / maxOf(width, height).toFloat()
                scaleFactor
            } else {
                1f
            }

            val resizedBitmap = if (scale < 1f) {
                Bitmap.createScaledBitmap(
                    originalBitmap,
                    (width * scale).toInt(),
                    (height * scale).toInt(),
                    true
                )
            } else {
                originalBitmap
            }

            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            FileOutputStream(tempFile).use { outputStream ->
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }

            val requestBody = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val response = okhttp3.OkHttpClient().newCall(
                okhttp3.Request.Builder()
                    .url(presignedUrlResponse.data.url)
                    .put(requestBody)
                    .build()
            ).execute()

            if (response.isSuccessful) {
                Result.success(presignedUrlResponse.data.path)
            } else {
                Result.failure(Exception("Failed to upload image: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun clearTempUploadedImages() {
        val cacheDir = context.cacheDir
        cacheDir?.listFiles()?.forEach { file ->
            if (file.name.contains("upload_") && file.name.endsWith(".jpg")) {
                file.delete()
            }
        }
    }

    override suspend fun generateArt(request: AiArtRequest): Result<Response<AiArtResponse>> {
        return try {
            val response = ApiClient.genApi.generateAiArt(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(response)
                } ?: Result.failure(Exception("Generate art successful but response body is null"))
            } else {
                val errorMessage = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("Failed to generate image: ${response.code()} - $errorMessage"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 