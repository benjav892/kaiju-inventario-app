package com.example.kaiju

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.kaiju.ui.screen.PantallaListaProductos
import com.example.kaiju.ui.theme.ProductoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProductoTheme {
                PantallaListaProductos()
            }
        }
    }
}

