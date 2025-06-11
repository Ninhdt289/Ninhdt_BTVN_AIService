package com.example.ninhdt_btvn.ui.screen.pickphoto.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.ninhdt_btvn.ui.navigation.PickPhotoRoute
import com.example.ninhdt_btvn.ui.screen.pickphoto.PickPhotoScreen

fun NavController.navigateToPickPhoto(navOptions: NavOptions) = navigate(route = PickPhotoRoute, navOptions)

fun NavGraphBuilder.pickPhotoScreen(
    onClose: () -> Unit,
    onImageSelected: (Long) -> Unit
) {
    composable(route = PickPhotoRoute.route) {
        PickPhotoScreen(
            onClose = onClose,
            onNext = { },
            onImageSelected = onImageSelected
        )
    }
}