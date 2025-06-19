package com.example.aisevice.data.remote.impl

import com.example.aisevice.data.remote.model.BaseResponse
import com.example.aisevice.data.remote.model.StyleResponse
import com.example.aisevice.data.remote.repository.StyleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StyleRepositoryImpl : StyleRepository {
    override suspend fun getStyles(): Result<BaseResponse<StyleResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = com.example.aisevice.data.client.ApiClient.styleApi.getStyles()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}