package com.example.ninhdt_btvn.ui.screen.main

import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.ninhdt_btvn.ui.navigation.MainRoute

fun NavGraphBuilder.mainScreen(
    onImageGenerated: (String) -> Unit = {},
    onGenerate: () -> Unit = {},
) {
    composable(route = MainRoute.route) {
            backStackEntry ->
        val selectedImageUri = backStackEntry
            .savedStateHandle
            .getLiveData<String>("selected_image_uri")
            .observeAsState()

        MainScreen(
            imageUri = selectedImageUri.value,
            onGenerate = {
                onGenerate()
            },
            onImageSelected = { uri ->
                onImageGenerated(uri)
            }

        )
    }
}