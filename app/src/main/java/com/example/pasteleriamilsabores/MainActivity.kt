package com.example.pasteleriamilsabores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.pasteleriamilsabores.navigation.AppNavigation
import com.example.pasteleriamilsabores.ui.theme.PasteleriaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PasteleriaTheme {
                AppNavigation()
            }
        }
    }
}
