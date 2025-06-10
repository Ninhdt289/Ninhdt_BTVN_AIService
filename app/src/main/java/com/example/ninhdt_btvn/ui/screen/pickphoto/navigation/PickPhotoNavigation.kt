package com.example.ninhdt_btvn.ui.screen.pickphoto.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.ninhdt_btvn.ui.navigation.AiGenScreen
import com.example.ninhdt_btvn.ui.navigation.PickPhotoRoute
import com.example.ninhdt_btvn.ui.screen.main.MainScreen

fun NavController.navigateToPickPhoto(navOptions: NavOptions) = navigate(route = PickPhotoRoute, navOptions)

fun NavGraphBuilder.mainScreen(
) {
    composable<PickPhotoRoute> {
      /*  PickPhotoScreen()*/
    }
}