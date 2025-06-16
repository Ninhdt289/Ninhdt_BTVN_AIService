package com.example.ninhdt_btvn.data.api

import com.example.aisevice.data.remote.model.PresignedUrlResponse
import com.example.aisevice.data.remote.request.AiArtRequest
import com.example.aisevice.data.remote.model.BaseResponse
import com.example.aisevice.data.remote.request.AiArtResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body


interface AIServiceApi {
   /* @GET("api/v5/image-ai/presigned-link")
    suspend fun getPresignedUrl(): PresignedUrlResponse*/

    @POST("api/v5/image-ai")
    suspend fun generateAiArt(@Body request: AiArtRequest): Response<AiArtResponse>
}
