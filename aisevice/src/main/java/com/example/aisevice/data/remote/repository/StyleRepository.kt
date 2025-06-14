package com.example.aisevice.data.remote.repository

import com.example.aisevice.data.remote.model.BaseResponse
import com.example.aisevice.data.remote.model.StyleResponse

interface StyleRepository {
    suspend fun getStyles(): Result<BaseResponse<StyleResponse>>
}