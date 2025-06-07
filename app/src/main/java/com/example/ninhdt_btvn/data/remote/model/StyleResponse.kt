package com.example.ninhdt_btvn.data.remote.model

import com.google.gson.annotations.SerializedName

data class StyleItem(
    @SerializedName("_id") val id: String,
    @SerializedName("project") val project: String,
    @SerializedName("name") val name: String,
    @SerializedName("key") val key: String,
    @SerializedName("config") val config: Config,
    @SerializedName("mode") val mode: String,
    @SerializedName("version") val version: String,
    @SerializedName("metadata") val metadata: List<Any>,
    @SerializedName("priority") val priority: Long,
    @SerializedName("thumbnailApp") val thumbnailApp: List<Any>,
    @SerializedName("categories") val categories: List<Any>,
    @SerializedName("segmentId") val segmentId: String,
    @SerializedName("subscriptionType") val subscriptionType: String,
    @SerializedName("aiFamily") val aiFamily: String,
    @SerializedName("styleType") val styleType: String,
    @SerializedName("imageSize") val imageSize: String,
    @SerializedName("baseModel") val baseModel: String,
    @SerializedName("shouldCollectImage") val shouldCollectImage: Boolean,
    @SerializedName("__v") val versionCode: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("pricing") val pricing: Int
)

data class Config(
    @SerializedName("mode") val mode: Int,
    @SerializedName("baseModel") val baseModel: String,
    @SerializedName("style") val style: String,
    @SerializedName("positivePrompt") val positivePrompt: String? = null,
    @SerializedName("imageSize") val imageSize: String? = null
)

data class StyleResponse(
    @SerializedName("items") val items: List<StyleItem>
)