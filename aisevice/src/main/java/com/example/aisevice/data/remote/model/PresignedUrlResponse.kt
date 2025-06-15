package com.example.aisevice.data.remote.model

import com.google.gson.annotations.SerializedName

data class PresignedUrlResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: PresignedUrlData,
    @SerializedName("timestamp") val timestamp: Long
)

data class PresignedUrlData(
    @SerializedName("url") val url: String,
    @SerializedName("path") val path: String
) 