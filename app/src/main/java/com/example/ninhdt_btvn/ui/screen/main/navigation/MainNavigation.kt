package com.example.ninhdt_btvn.ui.screen.main.navigation

import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.ninhdt_btvn.ui.navigation.MainRoute
import com.example.ninhdt_btvn.ui.screen.main.MainScreen

fun NavGraphBuilder.mainScreen(
    onImageGenerated: (String) -> Unit = {},
    onOpenPickPhoto: () -> Unit = {},
) {
    composable(route = MainRoute.route) {
            backStackEntry ->
        val selectedImageUri = backStackEntry
            .savedStateHandle
            .getLiveData<String>("selected_image_uri")
            .observeAsState()

        MainScreen(
            imageUri = selectedImageUri.value,
            onOpenPickPhoto = {
                onOpenPickPhoto()
            },
            onImageSelected = { uri ->
                onImageGenerated(uri)
            }

        )
    }
}