package com.example.aisevice.data.client

import com.example.aisevice.data.interceptor.SignatureInterceptor
import com.example.aisevice.data.remote.request.AIServiceApi
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

object ApiClient : KoinComponent {
    private const val BASE_URL = "https://api-style-manager.apero.vn/"
    private const val BASE_URL_GEN = "https://api-img-gen-wrapper.apero.vn/"
    const val REQUEST_TIMEOUT: Long = 30
    private val httpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(SignatureInterceptor())
            .addInterceptor(createLoggingInterceptor())
            .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    private val retrofit by lazy { buildRetrofit(baseUrl = BASE_URL) }
    private val retrofitGen by lazy { buildRetrofit(baseUrl = BASE_URL_GEN) }
    val styleApi: AIServiceApi by lazy {
        retrofit.create(AIServiceApi::class.java)
    }
    val genApi: AIServiceApi by lazy {
        retrofitGen.create(AIServiceApi::class.java)
    }

    private fun buildRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gsonConfig))
            .build()
    }

    private val gsonConfig = GsonBuilder().create()

    fun createLoggingInterceptor(): HttpLoggingInterceptor {
        val logLevel = if (true) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return HttpLoggingInterceptor().apply { level = logLevel }
    }
}