package com.example.ninhdt_btvn.data.remote.repository

import android.util.Log
import com.example.ninhdt_btvn.data.client.ApiClient
import com.example.ninhdt_btvn.data.remote.model.StyleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StyleRepository {
    suspend fun getStyles(): Result<StyleResponse> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.styleApi.getStyles()
            Log.d(TAG, "Fetched ${response.items} styles")
            response.items.forEachIndexed { index, style ->
                Log.d(TAG, "Style ${index + 1}: ID=${style.id}, Name=${style.name}, Subscription=${style.subscriptionType}")
            }
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching styles: ${e.message}", e)
            Result.failure(e)
        }
    }
    companion object {
        private const val TAG = "StyleRepository"
    }
}