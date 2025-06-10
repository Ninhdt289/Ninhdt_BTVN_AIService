package com.example.ninhdt_btvn.ui.screen.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.ninhdt_btvn.ui.navigation.MainRoute


fun NavController.navigateToMain(navOptions: NavOptions) = navigate(route = MainRoute, navOptions)

fun NavGraphBuilder.mainScreen(
) {
    composable<MainRoute> {
        MainScreen()
    }
}