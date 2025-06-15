package com.example.aisevice.data.remote.model

data class PresignedUrlResponse(
    val statusCode: Int,
    val message: String,
    val data: PresignedUrlData,
    val timestamp: Long
)

data class PresignedUrlData(
    val url: String,
    val path: String
) 