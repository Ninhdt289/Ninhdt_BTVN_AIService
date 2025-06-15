package com.example.aisevice.data.remote.repository

import android.content.Context
import com.example.aisevice.data.remote.model.PresignedUrlResponse
import com.example.aisevice.data.remote.model.ResponseState
import retrofit2.Response
import java.io.File

interface HandlerApiWithImageRepo {
    suspend fun callApiWithImage(
        pathImage: String,
        preSignLink: suspend () -> Response<PresignedUrlResponse>,
        callApi: suspend (String) -> ResponseState<String, Throwable>,
        folderName: String,
        context: Context

    ): ResponseState<File, Throwable>

    suspend fun callApiWithImages(
        pathImage: List<String>,
        preSignLink: suspend () -> Response<PresignedUrlResponse>,
        callApi: suspend (List<String>) -> ResponseState<String, Throwable>,
        folderName: String,
        context: Context

    ): ResponseState<File, Throwable>

    suspend fun pushImageToServer(
        url: String,
        file: String,
        folderName: String,
        context: Context
    ): ResponseState<String, Throwable>
}