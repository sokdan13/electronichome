package com.example.electronichome

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.example.electronichome.presentation.navigation.AppNavGraph

import com.example.electronichome.presentation.theme.ElectronichomeTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MyApplication : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Color(0xFFF57C00).toArgb()
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color(0xFFFFFFFF).toArgb(),
                Color(0xFFFFFFFF).toArgb()
            )
        )
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        WindowInsetsControllerCompat(window, window.decorView).apply {
//            hide(WindowInsetsCompat.Type.NAVIGATION_BARS)
//        }

        setContent {
            ElectronichomeTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}