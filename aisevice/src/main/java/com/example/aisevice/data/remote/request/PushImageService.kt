package com.example.aisevice.data.remote.request

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url

interface PushImageService {

    @PUT
    suspend fun pushImageToServer(
        @Url url: String,
        @Body file: RequestBody
    ): Response<ResponseBody>
}