package com.apero.aigenerate.network.model.segment

import com.google.gson.annotations.SerializedName

data class SegmentRequest(
    @SerializedName("file")
    val file: String
)