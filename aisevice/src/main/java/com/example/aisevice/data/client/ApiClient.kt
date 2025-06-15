package com.example.aisevice.data.client

import com.example.aisevice.data.remote.request.StyleAPI
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api-style-manager.apero.vn/"
    private const val BASE_URL_GEN = "https://api-img-gen-wrapper.apero.vn/"

    private val retrofit by lazy { buildRetrofit(baseUrl = BASE_URL) }
    private val retrofitGen by lazy { buildRetrofit(baseUrl = BASE_URL_GEN) }
    val styleApi: StyleAPI by lazy {
        retrofit.create(StyleAPI::class.java)
    }
    val genApi: StyleAPI by lazy {
        retrofitGen.create(StyleAPI::class.java)
    }




    private fun buildRetrofit(baseUrl: String ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(buildClient())
            .addConverterFactory(GsonConverterFactory.create(gsonConfig))
            .build()
    }

    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    private val gsonConfig = GsonBuilder().create()
}