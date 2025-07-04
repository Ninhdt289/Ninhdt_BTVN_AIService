package com.example.aisevice.data.remote.request

import com.example.aisevice.data.remote.model.BaseResponse
import com.example.aisevice.data.remote.model.PresignedUrlResponse
import com.example.aisevice.data.remote.model.StyleResponse
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AIServiceApi {
    @GET("category?project=techtrek&segmentValue=IN&styleType=imageToImage&isApp=true")
    suspend fun getStyles(): BaseResponse<StyleResponse>

    @GET("api/v5/image-ai/presigned-link")
    suspend fun getPresignedUrl(): PresignedUrlResponse

    @POST("api/v5/image-ai")
    suspend fun generateAiArt(@Body request: AiArtRequest): Response<AiArtResponse>
}
