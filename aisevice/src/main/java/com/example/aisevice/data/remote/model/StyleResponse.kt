package com.example.aisevice.data.remote.model

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("data") val data: T
)

data class StyleCategory(
    @SerializedName("name") val name: String,
    @SerializedName("styles") val styles: List<StyleItem>
)

data class StyleItem(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("key") val key: String,
    @SerializedName("config") val config: Config,
)

data class Config(
    @SerializedName("positivePrompt") val positivePrompt: String? = null,
    @SerializedName("negativePrompt") val negativePrompt: String? = null,
)

data class StyleResponse(
    @SerializedName("items") val items:  List<StyleCategory>)