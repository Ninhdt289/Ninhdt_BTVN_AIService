package com.example.ninhdt_btvn.ui.screen.pickphoto.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.aisevice.data.local.model.DeviceImage
import com.example.ninhdt_btvn.ui.navigation.PickPhotoRoute
import com.example.ninhdt_btvn.ui.screen.pickphoto.PickPhotoScreen

fun NavGraphBuilder.pickPhotoScreen(
    onClose: () -> Unit,
    onImageSelected: (DeviceImage) -> Unit
) {
    composable(route = PickPhotoRoute.route) {
        PickPhotoScreen(
            onClose = onClose,
            onNext = { },
            onImageSelected = onImageSelected
        )
    }
}