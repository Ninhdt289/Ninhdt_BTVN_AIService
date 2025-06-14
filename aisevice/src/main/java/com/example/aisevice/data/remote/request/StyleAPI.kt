package com.example.aisevice.data.remote.request

import com.example.aisevice.data.remote.model.BaseResponse
import com.example.aisevice.data.remote.model.StyleResponse
import retrofit2.http.GET

interface StyleAPI {
    @GET("category?project=techtrek&segmentValue=IN&styleType=imageToImage&isApp=true")
    suspend fun getStyles(): BaseResponse<StyleResponse>
}