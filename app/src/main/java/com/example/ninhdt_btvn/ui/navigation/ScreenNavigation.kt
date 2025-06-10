package com.example.ninhdt_btvn.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.ninhdt_btvn.ui.screen.main.mainScreen
import com.example.ninhdt_btvn.ui.screen.pickphoto.navigation.pickPhotoScreen
import com.example.ninhdt_btvn.ui.screen.result.navigation.resultScreen

@Composable
fun ScreenNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AiGenScreen.PickPhotoScreen.route
    ) {
        mainScreen()

        pickPhotoScreen()

        resultScreen()
    }
}