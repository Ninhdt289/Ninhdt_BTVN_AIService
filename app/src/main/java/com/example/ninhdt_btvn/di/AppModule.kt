package com.example.ninhdt_btvn.di
import com.example.ninhdt_btvn.ui.screen.main.MainViewModel
import com.example.ninhdt_btvn.ui.screen.pickphoto.PickPhotoViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::PickPhotoViewModel)
    

}