package com.example.ninhdt_btvn.ui.screen.result.navigation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.ninhdt_btvn.ui.navigation.ResultRoute
import com.example.ninhdt_btvn.ui.screen.result.ResultScreen

fun NavController.navigateToResult(imageUrl: String, navOptions: NavOptions) = 
    navigate("${ResultRoute.route}/$imageUrl", navOptions)

fun NavGraphBuilder.resultScreen(
    onBackClick: () -> Unit,
    onGenerateClick: () -> Unit
) {
    composable(
        route = "${ResultRoute.route}/{imageUrl}",
    ) { backStackEntry ->
        val imageUrl = backStackEntry.arguments?.getString("imageUrl")
        Log.d("ResultScreenninhdt22", "selectedImageUri: $imageUrl")
        ResultScreen(
            imageUrl = imageUrl,
            onBackClick = onBackClick,
            onGenerateClick = onGenerateClick
        )
    }
}