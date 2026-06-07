package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.screens.MainBoutiqueLayout
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.JewelleryViewModel
import com.example.ui.viewmodel.JewelleryViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: JewelleryViewModel by viewModels {
        JewelleryViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Lock dark styling for exclusive boutique luxury feel
            MyApplicationTheme(darkTheme = false, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainBoutiqueLayout(viewModel = viewModel)
                }
            }
        }
    }
}
