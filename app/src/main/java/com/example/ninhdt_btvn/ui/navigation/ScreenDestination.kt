package com.example.ninhdt_btvn.ui.navigation

import kotlinx.serialization.Serializable
import kotlin.reflect.KClass


sealed class AiGenScreen (val route: KClass<*>) {
    data object MainScreen: AiGenScreen(MainRoute::class)
    data object PickPhotoScreen: AiGenScreen(PickPhotoRoute::class)
    data object ResultScreen : AiGenScreen(ResultRoute::class)
}

@Serializable
data object MainRoute

@Serializable
data object PickPhotoRoute

@Serializable
data object ResultRoute

