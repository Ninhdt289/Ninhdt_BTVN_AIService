package com.example.ninhdt_btvn.data.api

import com.example.aisevice.data.remote.model.PresignedUrlResponse
import retrofit2.http.GET

interface AIServiceApi {
    @GET("api/v5/image-ai/presigned-link")
    suspend fun getPresignedUrl(): PresignedUrlResponse
} 