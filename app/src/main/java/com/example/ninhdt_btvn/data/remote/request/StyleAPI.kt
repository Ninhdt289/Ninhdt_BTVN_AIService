package com.example.ninhdt_btvn.data.remote.request

import com.example.ninhdt_btvn.data.remote.model.StyleResponse
import retrofit2.http.GET

interface StyleAPI {
    @GET("v2/styles?page=1&limit=100&project=Artimind")
    suspend fun getStyles(): StyleResponse
}