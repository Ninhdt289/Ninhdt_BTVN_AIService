package com.example.aisevice.di

import com.apero.aigenerate.network.repository.common.HandlerApiWithImageImpl
import com.example.aisevice.data.local.impl.ImageRepositoryImpl
import com.example.aisevice.data.local.repository.ImageRepository
import com.example.aisevice.data.remote.repository.StyleRepository
import com.example.aisevice.data.remote.impl.StyleRepositoryImpl
import com.example.aisevice.data.interceptor.SignatureInterceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.concurrent.TimeUnit
import com.example.aisevice.data.client.ApiClient
import com.example.aisevice.data.remote.repository.HandlerApiWithImageRepo
import com.example.aisevice.data.remote.request.PushImageService
import com.example.aisevice.data.remote.request.StyleAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    single<StyleAPI> {
        Retrofit.Builder()
            .baseUrl("https://api-img-gen-wrapper.apero.vn/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
            .create(StyleAPI::class.java)
    }
    single<PushImageService> {
        Retrofit.Builder()
            .baseUrl("https://api-img-gen-wrapper.apero.vn/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PushImageService::class.java)
    }

    single { StyleRepositoryImpl() } bind StyleRepository::class
    single { ImageRepositoryImpl(androidContext().contentResolver) } bind ImageRepository::class
    single{ HandlerApiWithImageImpl(get() ) } bind HandlerApiWithImageRepo::class
}