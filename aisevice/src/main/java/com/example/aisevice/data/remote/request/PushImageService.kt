package com.example.aisevice.data.remote.request

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url
import com.example.aisevice.data.remote.request.AiArtRequest
import com.example.aisevice.data.remote.model.BaseResponse
import retrofit2.http.POST

interface PushImageService {

    @PUT
    suspend fun pushImageToServer(
        @Url url: String,
        @Body file: RequestBody
    ): Response<ResponseBody>

    @POST("api/v5/image-ai")
    suspend fun genArtAi(@Body request: AiArtRequest): retrofit2.Response<BaseResponse<String>>
}