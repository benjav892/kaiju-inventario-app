package com.example.kaiju.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kaiju.ui.components.TarjetaMovimiento
import com.example.kaiju.viewmodel.InventarioViewModel

/**
 * Pantalla que despliega el historial completo de movimientos de inventario.
 * Utiliza LazyColumn para renderizar de forma eficiente cada TarjetaMovimiento.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorialMovimientos(
    viewModel: InventarioViewModel,
    onNavegarARegistrar: () -> Unit,
    onVolver: () -> Unit
) {
    val movimientos by viewModel.movimientos.collectAsState()
    val productos by viewModel.productos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Movimientos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavegarARegistrar) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nuevo Movimiento"
                )
            }
        }
    ) { innerPadding ->
        if (movimientos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay movimientos registrados.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(
                    items = movimientos,
                    key = { it.id }
                ) { movimiento ->
                    val producto = productos.find { it.codigo == movimiento.codigoProducto }
                    TarjetaMovimiento(
                        movimiento = movimiento,
                        producto = producto
                    )
                }
            }
        }
    }
}