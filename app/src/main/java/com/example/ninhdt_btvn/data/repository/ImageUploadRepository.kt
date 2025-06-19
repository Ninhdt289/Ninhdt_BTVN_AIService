package com.example.ninhdt_btvn.data.repository

import android.net.Uri
import com.example.aisevice.data.remote.request.AiArtRequest
import com.example.aisevice.data.remote.request.AiArtResponse
import retrofit2.Response

interface ImageUploadRepository {
    suspend fun uploadImage(imageUri: Uri): Result<String>

    suspend fun generateArt(request: AiArtRequest): Result<Response<AiArtResponse>>
}