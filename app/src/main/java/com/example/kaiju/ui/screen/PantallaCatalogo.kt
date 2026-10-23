package com.example.kaiju.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kaiju.ui.components.ListaProductos
import com.example.kaiju.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCatalogo(viewModel: InventarioViewModel,
                     onProductoClick: (String) -> Unit) {
    // Obtiene la lista desde el ViewModel de inventario
    val productosViewModel by viewModel.productos.collectAsState()

    // Estados de búsqueda y filtro
    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }

    val categorias = listOf("Todas") + productosViewModel.map { it.categoria }.distinct()

    // Filtro de productos
    val productosFiltrados = productosViewModel.filter { producto ->
        val coincidenciaBusqueda = producto.nombre.contains(textoBusqueda, ignoreCase = true) ||
                producto.codigo.contains(textoBusqueda, ignoreCase = true)
        val coincideCategoria = categoriaSeleccionada == "Todas" || producto.categoria == categoriaSeleccionada
        coincidenciaBusqueda && coincideCategoria
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Productos Kaiju") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Barra de Búsqueda
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                label = { Text("Buscar por nombre o código...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Filtros por Categoría (Chips)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(categorias) { cat ->
                    FilterChip(
                        selected = categoriaSeleccionada == cat,
                        onClick = { categoriaSeleccionada = cat },
                        label = { Text(cat) }
                    )
                }
            }

            //Lista de productos filtrados
            ListaProductos(productos = productosFiltrados,
                onProductoClick = onProductoClick)
        }
    }
}
