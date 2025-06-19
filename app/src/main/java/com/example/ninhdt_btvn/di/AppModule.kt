package com.example.ninhdt_btvn.di

import com.example.ninhdt_btvn.data.repository.ImageUploadRepository
import com.example.ninhdt_btvn.data.repository.ImageUploadRepositoryImpl
import com.example.ninhdt_btvn.ui.screen.main.MainViewModel
import com.example.ninhdt_btvn.ui.screen.pickphoto.PickPhotoViewModel
import org.koin.android.ext.koin.androidContext
import com.example.ninhdt_btvn.ui.screen.result.ResultViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { ImageUploadRepositoryImpl( androidContext()) } bind ImageUploadRepository::class
    viewModelOf(::MainViewModel)
    viewModelOf(::PickPhotoViewModel)
    viewModelOf(::ResultViewModel)
}