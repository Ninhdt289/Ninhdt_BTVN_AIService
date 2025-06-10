package com.example.ninhdt_btvn.ui.navigation

sealed class AiGenScreen(val route: String) {
    data object MainScreen : AiGenScreen(MainRoute.route)
    data object PickPhotoScreen : AiGenScreen(PickPhotoRoute.route)
    data object ResultScreen : AiGenScreen(ResultRoute.route)
}

object MainRoute {
    const val route = "main"
}

object PickPhotoRoute {
    const val route = "pick_photo"
}

object ResultRoute {
    const val route = "result"
}


