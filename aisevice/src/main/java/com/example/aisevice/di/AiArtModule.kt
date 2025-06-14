package com.example.aisevice.di

import com.example.aisevice.data.local.impl.ImageRepositoryImpl
import com.example.aisevice.data.local.repository.ImageRepository
import com.example.aisevice.data.remote.repository.StyleRepository
import com.example.aisevice.data.remote.impl.StyleRepositoryImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val  aiArtModule = module {
    single { StyleRepositoryImpl() } bind StyleRepository::class
   // single { ImageRepositoryImpl(get())} bind ImageRepository::class
}