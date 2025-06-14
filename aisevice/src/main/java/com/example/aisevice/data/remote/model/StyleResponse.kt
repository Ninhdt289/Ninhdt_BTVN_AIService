package com.example.aisevice.data.remote.model

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("data") val data: T
)

data class StyleCategory(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("priority") val priority: Double,
    @SerializedName("project") val project: String,
    @SerializedName("segment") val segment: String,
    @SerializedName("styles") val styles: List<StyleItem>
)

data class StyleItem(
    @SerializedName("_id") val id: String,
    @SerializedName("project") val project: String,
    @SerializedName("name") val name: String,
    @SerializedName("key") val key: String,
    @SerializedName("config") val config: Config,
    @SerializedName("mode") val mode: String,
    @SerializedName("version") val version: String,
    @SerializedName("metadata") val metadata: List<Any>,
    @SerializedName("priority") val priority: Double,
    @SerializedName("thumbnailApp") val thumbnailApp: List<ThumbnailItem>,
    @SerializedName("categories") val categories: List<String>,
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
    @SerializedName("pricing") val pricing: Int,
    @SerializedName("thumbnail") val thumbnail: ThumbnailUrls? = null,
    @SerializedName("domain") val domain: Domain? = null
)

data class Config(
    @SerializedName("mode") val mode: Int,
    @SerializedName("baseModel") val baseModel: String,
    @SerializedName("style") val style: String,
    @SerializedName("positivePrompt") val positivePrompt: String? = null,
    @SerializedName("negativePrompt") val negativePrompt: String? = null,
    @SerializedName("imageSize") val imageSize: String? = null,
    @SerializedName("fixOpenpose") val fixOpenpose: Boolean? = null,
    @SerializedName("alpha") val alpha: String? = null,
    @SerializedName("strength") val strength: String? = null,
    @SerializedName("guidanceScale") val guidanceScale: String? = null,
    @SerializedName("numInferenceSteps") val numInferenceSteps: String? = null
)

data class ThumbnailItem(
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("thumbnail_type") val thumbnailType: String,
    @SerializedName("_id") val id: String
)

data class ThumbnailUrls(
    @SerializedName("before") val before: String? = null,
    @SerializedName("after") val after: String? = null,
    @SerializedName("key") val key: String? = null,
    @SerializedName("preview_style") val previewStyle: String? = null,
    @SerializedName("reminder_after") val reminderAfter: String? = null,
    @SerializedName("reminder_before") val reminderBefore: String? = null,
    @SerializedName("noti") val noti: String? = null
)

data class Domain(
    @SerializedName("_id") val id: String,
    @SerializedName("displayName") val displayName: String,
    @SerializedName("name") val name: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("baseUrl") val baseUrl: String,
    @SerializedName("priority") val priority: Double,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("__v") val versionCode: Int
)

data class StyleResponse(
    @SerializedName("items") val items:  List<StyleCategory>)