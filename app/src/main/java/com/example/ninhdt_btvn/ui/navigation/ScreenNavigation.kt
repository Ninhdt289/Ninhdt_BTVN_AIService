package com.example.ninhdt_btvn.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.ninhdt_btvn.ui.screen.main.mainScreen
import com.example.ninhdt_btvn.ui.screen.pickphoto.navigation.pickPhotoScreen
import com.example.ninhdt_btvn.ui.screen.result.navigation.resultScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
            onOpenPickPhoto = { navController.navigate(AiGenScreen.PickPhotoScreen.route) },
            onImageGenerated = { imageUrl ->
                val encodedUrl = URLEncoder.encode(imageUrl, StandardCharsets.UTF_8.toString())
                navController.navigate("${AiGenScreen.ResultScreen.route}/$encodedUrl") {
                    popUpTo(AiGenScreen.MainScreen.route)
                }
            }
        )

        pickPhotoScreen(
            onClose = { navController.popBackStack() },
            onImageSelected = { selectedImage ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("selected_image_uri", selectedImage.uri.toString())
                navController.popBackStack()
            }
        )

        resultScreen(
            onBackClick = { navController.popBackStack() },
        )
    }
}