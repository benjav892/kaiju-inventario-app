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
    Producto(
        codigo = "k1", nombre = "producto1", descripcion = "primer producto", categoria = "categoría1", tipo = "tipo1", precio = 15990, stockActual = 1, stockMinimo = 5, detalle = null),
    Producto(codigo = "k2", nombre = "producto2", descripcion = "segundo producto", categoria = "categoría1", tipo = "tipo2", precio = 8990, stockActual = 5, stockMinimo = 2, detalle = null),
    Producto(codigo = "k3", nombre = "producto3", descripcion = "tercer producto", categoria = "categoría2", tipo = "tipo1", precio = 24990, stockActual = 3, stockMinimo = 3, detalle = null
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCatalogo() {
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