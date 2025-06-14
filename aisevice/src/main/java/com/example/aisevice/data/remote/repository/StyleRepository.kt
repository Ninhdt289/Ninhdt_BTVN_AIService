package com.example.aisevice.data.remote.repository

import android.util.Log
import com.example.aisevice.data.client.ApiClient
import com.example.aisevice.data.remote.model.BaseResponse
import com.example.aisevice.data.remote.model.StyleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StyleRepository {
    suspend fun getStyles(): Result<BaseResponse<StyleResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = com.example.aisevice.data.client.ApiClient.styleApi.getStyles()
            val styles = response.data.items
            Log.d(TAG, "Fetched ${styles.size} styles")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching styles: ${e.message}", e)
            Result.failure(e)
        }
    }
    companion object {
        private const val TAG = "StyleRepositoriiiii"
    }
}