package com.example.aisevice.data.remote.request

import com.example.aisevice.data.remote.model.BaseResponse
import com.example.aisevice.data.remote.model.PresignedUrlResponse
import com.example.aisevice.data.remote.model.StyleResponse
import retrofit2.http.GET

interface StyleAPI : PushImageService{
    @GET("category?project=techtrek&segmentValue=IN&styleType=imageToImage&isApp=true")
    suspend fun getStyles(): BaseResponse<StyleResponse>

    @GET("api/v5/image-ai/presigned-link")
    suspend fun getPresignedUrl(): PresignedUrlResponse
}
