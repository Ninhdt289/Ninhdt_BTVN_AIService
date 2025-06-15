package com.apero.aigenerate.network.model.segment

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ResponseSegment(
    @SerializedName("data") val data: List<SegmentationRequest> = emptyList()
)

@Keep
data class SegmentationRequest(
    @SerializedName("className") val className: String,
    @SerializedName("xLeft") val xLeft: Int,
    @SerializedName("yTop") val yTop: Int,
    @SerializedName("xRight") val xRight: Int,
    @SerializedName("yBottom") val yBottom: Int,
    @SerializedName("matrix") val matrix: List<MatrixSegmentRequest>,
)

@Keep
data class MatrixSegmentRequest(
    @SerializedName("x") val x: Int,
    @SerializedName("y") val y: Int,
)
