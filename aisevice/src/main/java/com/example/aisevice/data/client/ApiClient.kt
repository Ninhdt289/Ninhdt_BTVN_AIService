package com.example.aisevice.data.client

import com.apero.beauty_full.utils.data.network.interceptor.SignatureInterceptor
import com.example.aisevice.data.remote.request.StyleAPI
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://api-style-manager.apero.vn/"
    private const val BASE_URL_GEN = "https://api-img-gen-wrapper.apero.vn/"
    const val REQUEST_TIMEOUT: Long = 30
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
          //  .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gsonConfig))
            .build()
    }

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(SignatureInterceptor())
            .addInterceptor(createLoggingInterceptor())
            .build()
    }
    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    private val gsonConfig = GsonBuilder().create()

    fun createLoggingInterceptor() : HttpLoggingInterceptor {
        val logLevel = if (true) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return HttpLoggingInterceptor().apply { level = logLevel }
    }
}