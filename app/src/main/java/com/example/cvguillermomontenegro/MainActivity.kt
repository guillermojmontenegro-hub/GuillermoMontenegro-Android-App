package com.example.cvguillermomontenegro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.example.cvguillermomontenegro.ui.navigation.AppNavHost
import com.example.cvguillermomontenegro.ui.theme.CVTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CVTheme {
                AppNavHost()
            }
        }
    }
}
