package com.example.ninhdt_btvn.ui.screen.result.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.ninhdt_btvn.ui.navigation.ResultRoute
import com.example.ninhdt_btvn.ui.screen.result.ResultScreen

fun NavGraphBuilder.resultScreen(
    onBackClick: () -> Unit,
) {
    composable(
        route = "${ResultRoute.route}/{imageUrl}",
    ) { backStackEntry ->
        val imageUrl = backStackEntry.arguments?.getString("imageUrl")
        ResultScreen(
            imageUrl = imageUrl,
            onBackClick = onBackClick,
        )
    }
}