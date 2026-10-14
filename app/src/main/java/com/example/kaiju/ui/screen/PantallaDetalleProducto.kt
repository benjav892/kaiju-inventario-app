package com.example.kaiju.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.ui.components.IndicadorStock
import com.example.kaiju.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleProducto(
    codigoProducto: String,
    viewModel: InventarioViewModel,
    onVolver: () -> Unit
) {
    val productos by viewModel.productos.collectAsState()
    val movimientos by viewModel.movimientos.collectAsState()

    // Busca el producto coincidente
    val producto = productos.find { it.codigo == codigoProducto }

    // Filtra los movimientos de este producto y los ordena del más reciente al más antiguo (RF-04)
    val historialMovimientos = remember(movimientos, codigoProducto) {
        movimientos
            .filter { it.codigoProducto == codigoProducto }
            .sortedByDescending { it.id } // Orden descendente por id/fecha
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(producto?.nombre ?: "Detalle de Producto") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (producto == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Producto no encontrado")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Seccion A: Tarjeta con detalles del Producto
                item {
                    TarjetaDetalleInfo(producto = producto)
                }

                // Encabezado Historial
                item {
                    Text(
                        text = "Historial de Movimientos (${historialMovimientos.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Seccion B: Lista de Movimientos (Mas reciente al mas antiguo)
                if (historialMovimientos.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(
                                text = "Sin movimientos registrados para este producto.",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(historialMovimientos) { movimiento ->
                        TarjetaMovimientoItem(movimiento = movimiento)
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaDetalleInfo(producto: Producto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IndicadorStock(
                    stockActual = producto.stockActual,
                    stockMinimo = producto.stockMinimo
                )
            }

            HorizontalDivider()

            Text(text = "Código: ${producto.codigo}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Categoría: ${producto.categoria}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Tipo: ${producto.tipo}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Precio: $${producto.precio}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Stock Mínimo de Alerta: ${producto.stockMinimo}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (producto.descripcion.isNotBlank()) {
                Text(
                    text = "Descripción: ${producto.descripcion}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            producto.detalle?.let { det ->
                if (det.isNotBlank()) {
                    Text(
                        text = "Detalle adicional: $det",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaMovimientoItem(movimiento: Movimiento) {
    val colorBadge = when (movimiento.tipo) {
        TipoMovimiento.ENTRADA -> Color(0xFF2E7D32)
        TipoMovimiento.SALIDA_VENTA -> Color(0xFFC62828)
        TipoMovimiento.SALIDA_CONSUMO -> Color(0xFFEF6C00)
        TipoMovimiento.AJUSTE -> Color(0xFF1565C0)
    }

    val signo = when (movimiento.tipo) {
        TipoMovimiento.ENTRADA -> "+"
        TipoMovimiento.SALIDA_VENTA, TipoMovimiento.SALIDA_CONSUMO -> "-"
        TipoMovimiento.AJUSTE -> "="
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = colorBadge,
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = movimiento.tipo.name.replace("_", " "),
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "$signo${movimiento.cantidad} unidades",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorBadge
                )
            }

            Text(
                text = "Registrado por: ${movimiento.usuario}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (movimiento.motivo.isNotBlank()) {
                Text(
                    text = "Motivo: ${movimiento.motivo}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}