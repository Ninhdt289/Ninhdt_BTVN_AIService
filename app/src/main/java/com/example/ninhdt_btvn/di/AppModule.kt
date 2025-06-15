package com.example.ninhdt_btvn.di

import com.example.aisevice.data.remote.request.StyleAPI
import com.example.ninhdt_btvn.data.api.AIServiceApi
import com.example.ninhdt_btvn.data.repository.ImageUploadRepository
import com.example.ninhdt_btvn.data.repository.ImageUploadRepositoryImpl
import com.example.ninhdt_btvn.ui.screen.main.MainViewModel
import com.example.ninhdt_btvn.ui.screen.pickphoto.PickPhotoViewModel
import org.koin.android.ext.koin.androidContext

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    single<AIServiceApi>{
        Retrofit.Builder()
            .baseUrl("https://api-img-gen-wrapper.apero.vn/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
            .create(AIServiceApi::class.java)
    }
    single { ImageUploadRepositoryImpl( androidContext()) } bind ImageUploadRepository::class
    viewModelOf(::MainViewModel)
    viewModelOf(::PickPhotoViewModel)
}