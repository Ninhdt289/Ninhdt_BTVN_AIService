package com.example.aisevice.data.remote.request

import com.google.gson.annotations.SerializedName

data class AiArtRequest(
    @SerializedName("file")
    val file: String,

    @SerializedName("style")
    val style: String? = null,

    @SerializedName("styleId")
    val styleId: String,

    @SerializedName("positivePrompt")
    val positivePrompt: String? = null,

    @SerializedName("negativePrompt")
    val negativePrompt: String? = null,

    @SerializedName("imageSize")
    val imageSize: Int? = null,

    @SerializedName("fixHeight")
    val fixHeight: Int? = null,

    @SerializedName("fixWidth")
    val fixWidth: Int? = null,

    @SerializedName("fixWidthAndHeight")
    val fixWidthAndHeight: Boolean? = null,

    @SerializedName("useControlnet")
    val useControlnet: Boolean? = null,

    @SerializedName("applyPulid")
    val applyPulid: Boolean? = null,

    @SerializedName("seed")
    val seed: Int? = null,

    @SerializedName("fastMode")
    val fastMode: Boolean? = null
)