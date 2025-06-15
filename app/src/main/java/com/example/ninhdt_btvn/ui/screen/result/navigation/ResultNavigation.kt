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

fun NavController.navigateToResult(navOptions: NavOptions) = navigate(route = ResultRoute, navOptions)

fun NavGraphBuilder.resultScreen(
    onBackClick: () -> Unit,
    onGenerateClick: () -> Unit
) {
    composable(route = ResultRoute.route) {
            backStackEntry ->
        val imageUrl by backStackEntry
            .savedStateHandle
            .getLiveData<String>("result_image_uri")
            .observeAsState()
        Log.d("ResultScreenninhdt22", "selectedImageUri: $imageUrl")
        ResultScreen(
            imageUrl = imageUrl,
            onBackClick = onBackClick,
            onGenerateClick = onGenerateClick
        )
    }
}