package com.example.ninhdt_btvn.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.ninhdt_btvn.ui.screen.main.mainScreen
import com.example.ninhdt_btvn.ui.screen.pickphoto.navigation.pickPhotoScreen
import com.example.ninhdt_btvn.ui.screen.result.navigation.resultScreen


@Composable
fun ScreenNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AiGenScreen.MainScreen.route
    ) {
        mainScreen(
            imageUri = navController.currentBackStackEntry
                ?.savedStateHandle
                ?.get<String>("selected_image_uri"),
            onGenerate = { navController.navigate(AiGenScreen.PickPhotoScreen.route) }
        )

        pickPhotoScreen(
            onClose = { navController.popBackStack() },
            onImageSelected = { selectedImage ->
                /*Log.d("SelectedImage", selectedImage.toString())
                navController.navigate(AiGenScreen.MainScreen.route)*/
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("selected_image_uri", selectedImage.uri.toString())
                navController.popBackStack()
            }
        )

        resultScreen()
    }
}