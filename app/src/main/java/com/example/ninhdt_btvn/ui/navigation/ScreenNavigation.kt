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
import org.koin.androidx.compose.koinViewModel
import com.example.ninhdt_btvn.ui.screen.result.ResultViewModel

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
            onGenerate = { navController.navigate(AiGenScreen.PickPhotoScreen.route) },
            onImageGenerated = { imageUrl ->
                Log.d("ScreenNavigationninhdt22", "Image generated: $imageUrl")
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("result_image_uri", imageUrl)
                navController.navigate(AiGenScreen.ResultScreen.route)
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
            onGenerateClick = { navController.navigate(AiGenScreen.MainScreen.route) }
        )
    }
}