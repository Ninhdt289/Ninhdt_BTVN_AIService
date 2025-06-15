package com.example.aisevice.di

import com.example.aisevice.data.local.impl.ImageRepositoryImpl
import com.example.aisevice.data.local.repository.ImageRepository
import com.example.aisevice.data.remote.repository.StyleRepository
import com.example.aisevice.data.remote.impl.StyleRepositoryImpl
import com.apero.beauty_full.utils.data.network.interceptor.SignatureInterceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.concurrent.TimeUnit
import com.example.aisevice.data.client.ApiClient

val aiArtModule = module {
    single { SignatureInterceptor() }
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<SignatureInterceptor>())
            .addInterceptor(ApiClient.createLoggingInterceptor())
            .connectTimeout(ApiClient.REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(ApiClient.REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(ApiClient.REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    single { StyleRepositoryImpl() } bind StyleRepository::class
    single { ImageRepositoryImpl(androidContext().contentResolver) } bind ImageRepository::class
}