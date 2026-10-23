package com.example.kaiju.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.repository.DatosDePrueba

import com.example.kaiju.ui.components.BotonCompartirReporteNativo
import com.example.kaiju.ui.components.BotonConPermisoCamara
import com.example.kaiju.ui.components.SelectorImagenProductoNativo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleProducto(
    producto: Producto?,
    movimientos: List<Movimiento>,
    puedeEditarProducto: Boolean = false,
    onVolver: () -> Unit,
    onIrARegistrarMovimiento: (String) -> Unit = {},
    onIrAEditarProducto: (String) -> Unit = {}
) {
    // filtrar los movimientos de este producto y ordenar del más reciente al más antiguo
    val historialOrdenado = producto?.let { prod ->
        movimientos
            .filter { it.codigoProducto == prod.codigo }
            .sortedByDescending { it.id }
    } ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(producto?.nombre ?: "Detalle de Producto") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver atrás"
                        )
                    }
                },
                actions = {
                    // solo el Administrador ve la acción para editar el producto
                    if (puedeEditarProducto && producto != null) {
                        IconButton(onClick = { onIrAEditarProducto(producto.codigo) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar producto"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (producto == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No se encontró la información del producto seleccionado.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = onVolver,
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text("Volver al catálogo")
                }
            }
        } else {
            val enAlertaStockBajo = producto.stockActual <= producto.stockMinimo

            // animación con proposito: el color de la tarjeta de stock cambia gradualmente según si está en alerta o normal
            val colorTarjetaStock by animateColorAsState(
                targetValue = if (enAlertaStockBajo) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                },
                label = "animacionColorStockDetalle"
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ficha técnica del producto
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                                    text = "Código: ${producto.codigo}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${producto.categoria} • ${producto.tipo}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = producto.nombre,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = producto.descripcion,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (producto.detalle.isNotBlank()) {
                                Text(
                                    text = "Especificación: ${producto.detalle}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = "Precio unitario: $${producto.precio} CLP",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // estado de existencias y alerta de reposición
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = colorTarjetaStock)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Stock disponible: ${producto.stockActual} unidades",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Stock mínimo exigido: ${producto.stockMinimo} unidades",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Icon(
                                    imageVector = if (enAlertaStockBajo) {
                                        Icons.Default.Warning
                                    } else {
                                        Icons.Default.CheckCircle
                                    },
                                    contentDescription = "Estado de stock",
                                    tint = if (enAlertaStockBajo) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                            }

                            // animación con propósito:
                            // AnimatedVisibility despliega la advertencia cuando stockActual <= stockMinimo
                            AnimatedVisibility(visible = enAlertaStockBajo) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "Producto en alerta de reposición: la cantidad disponible es igual o menor al stock mínimo.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // conexion de navegacion hacia PantallaMovimiento
                item {
                    Button(
                        onClick = { onIrARegistrarMovimiento(producto.codigo) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Registrar Movimiento de Stock")
                    }
                }
                // recursos nativos integrados en el Detalle
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BotonConPermisoCamara(
                            textoBoton = "Verificar Etiqueta con Cámara (Permiso)",
                            onPermisoConcedido = { _ -> },
                            onSinPermiso = { }
                        )
                        SelectorImagenProductoNativo()
                        BotonCompartirReporteNativo(
                            tituloReporte = "Ficha de Stock Kaiju - ${producto.codigo}",
                            contenidoReporte = "Producto: ${producto.nombre} (${producto.codigo})\n" +
                                    "Categoría: ${producto.categoria}\n" +
                                    "Stock disponible: ${producto.stockActual} uds. (Mínimo: ${producto.stockMinimo})\n" +
                                    "Precio: $${producto.precio} CLP",
                            textoBoton = "Compartir Ficha de Stock"
                        )
                    }
                }

                // encabezado de trazabilidad e historial de movimientos
                item {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = "Historial de Movimientos (${historialOrdenado.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Ordenado del más reciente al más antiguo. Por trazabilidad, los movimientos no se editan ni se borran.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // listado dinámico de movimientos del producto
                if (historialOrdenado.isEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Aún no se registran movimientos para este producto.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(historialOrdenado) { movimiento ->
                        TarjetaMovimientoHistorial(movimiento = movimiento)
                    }
                }
            }
        }
    }
}

// componente reutilizable que dibuja un registro de movimiento en el historial
@Composable
fun TarjetaMovimientoHistorial(movimiento: Movimiento) {
    val etiquetaTipo = when (movimiento.tipo) {
        TipoMovimiento.ENTRADA -> "Entrada de mercadería (+${movimiento.cantidad})"
        TipoMovimiento.SALIDA_VENTA -> "Salida por venta (-${movimiento.cantidad})"
        TipoMovimiento.SALIDA_CONSUMO -> "Salida por consumo interno (-${movimiento.cantidad})"
        TipoMovimiento.AJUSTE -> "Ajuste de inventario (${movimiento.cantidad} uds.)"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = etiquetaTipo,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = movimiento.fecha,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (movimiento.motivo.isNotBlank()) {
                Text(
                    text = "Motivo / Detalle: ${movimiento.motivo}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "Registrado por: ${movimiento.usuario}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// vista previa para verificar el diseño directamente en el ide
@Preview(showBackground = true)
@Composable
fun PreviewPantallaDetalleProducto() {
    PantallaDetalleProducto(
        producto = DatosDePrueba.productosIniciales[1], // Producto en alerta de stock bajo
        movimientos = DatosDePrueba.movimientosIniciales,
        puedeEditarProducto = true,
        onVolver = {}
    )
}