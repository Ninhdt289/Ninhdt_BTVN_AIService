package com.example.ninhdt_btvn.ui.screen.result.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.ninhdt_btvn.ui.navigation.ResultRoute
import com.example.ninhdt_btvn.ui.screen.result.ResultScreen

fun NavController.navigateToResult(navOptions: NavOptions) = navigate(route = ResultRoute, navOptions)

fun NavGraphBuilder.resultScreen(
) {
    composable<ResultRoute> {
        ResultScreen()
    }
}