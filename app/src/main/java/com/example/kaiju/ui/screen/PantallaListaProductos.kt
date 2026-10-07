package com.example.kaiju.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kaiju.model.Producto
import com.example.kaiju.ui.components.ListaProductos

private val listaDeEjemplo = listOf(
    Producto(id = "k1", nombre = "producto1", descripcion = "primer producto", stock = 1),
    Producto(id = "k2", nombre = "producto2", descripcion = "segundo producto", stock = 5),
    Producto(id = "k3", nombre = "producto3", descripcion = "tercer producto", stock = 3)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaProductos() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Productos Kaiju") })
        }) { padding ->
        ListaProductos(
            productos = listaDeEjemplo,
            modifier = Modifier.padding(padding)
        )
    }
}