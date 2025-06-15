package com.example.ninhdt_btvn.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.aisevice.data.client.ApiClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import com.example.aisevice.data.remote.request.AiArtRequest
import com.example.aisevice.data.remote.model.BaseResponse
import com.example.aisevice.data.remote.request.AiArtResponse

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.example.ninhdt_btvn.data.api.AIServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ImageUploadRepositoryImpl(
    private val context: Context
): ImageUploadRepository, KoinComponent {

    private val aiServiceApi: AIServiceApi by inject()

    override suspend fun uploadImage(imageUri: Uri): Result<String> =  withContext(Dispatchers.IO) {
        Log.d("ImageUploadRepositoryImpl", "uploadImage: $imageUri")
        return@withContext try {
            val presignedUrlResponse = ApiClient.genApi.getPresignedUrl()
            Log.d("ImageUploadRepositoryImpl", "getPresignedUrl response: $presignedUrlResponse")

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
                Log.d("ImageUploadRepositoryImpl", "uploadImage success, path: ${presignedUrlResponse.data.path}")
                Result.success(presignedUrlResponse.data.path)

            } else {
                Log.e("ImageUploadRepositoryImpl", "uploadImage failed: ${response.code} - ${response.message}")
                Result.failure(Exception("Failed to upload image: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e("ImageUploadRepositoryImpl", "uploadImage exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun generateArt(request: AiArtRequest): Result<Response<AiArtResponse>> {
        Log.d("ImageUploadRepositoryImpl", "generateArt called with request: $request")
        return try {
           val response = aiServiceApi.generateAiArt(request)
            if (response.isSuccessful) {
                Log.d("ImageUploadRepositoryImpl", "generateArt success. Response body: ${response.body()}")
                response.body()?.let { body ->
                    Result.success(response)
                } ?: Result.failure(Exception("Generate art successful but response body is null"))
            } else {
                val errorMessage = response.errorBody()?.string() ?: response.message()
                Log.e("ImageUploadRepositoryImpl", "generateArt failed: ${response.code()} - $errorMessage")
                Result.failure(Exception("Failed to generate image: ${response.code()} - $errorMessage"))
            }
        } catch (e: Exception) {
            Log.e("ImageUploadRepositoryImpl", "generateArt exception: ${e.message}", e)
            Result.failure(e)
        }
    }
} 