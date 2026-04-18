package com.example.electronichome

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.electronichome.presentation.navigation.NavGraph
import com.example.electronichome.presentation.theme.ElectronichomeTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MyApplication : Application()
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElectronichomeTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}