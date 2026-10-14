package com.example.kaiju.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.Producto
import com.example.kaiju.ui.components.IndicadorStock
import com.example.kaiju.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCatalogo(
    viewModel: InventarioViewModel,
    onProductoClick: (String) -> Unit = {}
) {
    val productos by viewModel.productos.collectAsState()

    // Estado local para búsqueda y filtro de categoría
    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }

    // Lista de categorías únicas extraídas dinámicamente
    val categorias = remember(productos) {
        listOf("Todas") + productos.map { it.categoria }.distinct()
    }

    // Filtrado de productos según búsqueda (nombre o código) y categoría
    val productosFiltrados = remember(textoBusqueda, categoriaSeleccionada, productos) {
        productos.filter { producto ->
            val coincideBusqueda = producto.nombre.contains(textoBusqueda, ignoreCase = true) ||
                    producto.codigo.contains(textoBusqueda, ignoreCase = true)
            val coincideCategoria = categoriaSeleccionada == "Todas" || producto.categoria == categoriaSeleccionada
            coincideBusqueda && coincideCategoria
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Catálogo Kaiju") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // 1. Buscador por nombre o código (RF-03)
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                label = { Text("Buscar producto por nombre o código") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // 2. Chips de filtro por categoría (RF-03)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(categorias) { categoria ->
                    FilterChip(
                        selected = categoriaSeleccionada == categoria,
                        onClick = { categoriaSeleccionada = categoria },
                        label = { Text(categoria) }
                    )
                }
            }

            // 3. Listado eficiente con LazyColumn
            if (productosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron productos",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(productosFiltrados) { producto ->
                        TarjetaProductoItem(
                            producto = producto,
                            onClick = { onProductoClick(producto.codigo) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaProductoItem(
    producto: Producto,
    onClick: () -> Unit
) {
    val esStockBajo = producto.stockActual <= producto.stockMinimo

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (esStockBajo) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                // Marca/Badge visual de Stock Bajo (RN-06 / RF-09)
                IndicadorStock(
                    stockActual = producto.stockActual,
                    stockMinimo = producto.stockMinimo
                )
            }

            Text(
                text = "Código: ${producto.codigo} | Categoría: ${producto.categoria}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Precio: $${producto.precio}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}