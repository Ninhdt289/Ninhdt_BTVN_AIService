package com.example.ninhdt_btvn.data.client

import com.example.ninhdt_btvn.data.remote.request.StyleAPI
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api-style-manager.apero.vn/"

    private val retrofit by lazy { buildRetrofit() }

    val styleApi: StyleAPI by lazy {
        retrofit.create(StyleAPI::class.java)
    }

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(buildClient())
            .addConverterFactory(GsonConverterFactory.create(gsonConfig))
            .build()
    }

    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    private val gsonConfig = GsonBuilder().create()
}