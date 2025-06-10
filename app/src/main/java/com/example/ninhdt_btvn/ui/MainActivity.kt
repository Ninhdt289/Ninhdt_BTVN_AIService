package com.example.ninhdt_btvn.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ninhdt_btvn.ui.navigation.ScreenNavigation
import com.example.ninhdt_btvn.ui.screen.main.MainScreen
import com.example.ninhdt_btvn.ui.theme.NinhdtBTVNAIServicePublicTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NinhdtBTVNAIServicePublicTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ScreenNavigation(modifier = Modifier.padding(innerPadding))
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NinhdtBTVNAIServicePublicTheme {
        MainScreen()
    }
}