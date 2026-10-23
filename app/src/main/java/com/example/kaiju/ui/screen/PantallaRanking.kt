package com.example.kaiju.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

//Clases de datos auxiliares para representar cada fila del reporte de ventas
data class ItemRankingProducto(
val producto: Producto,
val unidadesVendidas: Int,
val montoTotalVentas: Int
)

data class ItemResumenCategoria(
val categoria: String,
val cantidadProductos: Int,
val unidadesVendidas: Int,
val montoTotalCategoria: Int
)


// pantalla del modulo propio de Kaiju: Ranking de ventas y Tendencias por categoría
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRanking(
    productos: List<Producto>,
    movimientos: List<Movimiento>,
    onVolver: () -> Unit = {},
    onProductoClick: (String) -> Unit = {}
) {
// estado local de UI con remember + mutableStateOf
// controla si se ordena por más vendidos (true) o menos vendidos (false) y si se despliega el resumen por categoría.
var mostrarMasVendidos by remember { mutableStateOf(true) }
var mostrarResumenCategorias by remember { mutableStateOf(true) }

// filtrar solo los movimientos que corresponden a ventas reales
val movimientosVenta = movimientos.filter { it.tipo == TipoMovimiento.SALIDA_VENTA }

// transformar cada producto con map y sumar sus unidades vendidas con reduce
val listaRankingBase = productos.map { producto ->
    val ventasDeEsteProducto = movimientosVenta
        .filter { it.codigoProducto == producto.codigo }
        .map { it.cantidad }

    val totalUnidades = if (ventasDeEsteProducto.isEmpty()) {
        0
    } else {
        ventasDeEsteProducto.reduce { acumulado, actual -> acumulado + actual }
    }

    ItemRankingProducto(
        producto = producto,
        unidadesVendidas = totalUnidades,
        montoTotalVentas = totalUnidades * producto.precio
    )
}

// ordenar segun la seleccion del usuario con sortedByDescending o sortedBy
val rankingOrdenado = if (mostrarMasVendidos) {
    listaRankingBase.sortedByDescending { it.unidadesVendidas }
} else {
    listaRankingBase.sortedBy { it.unidadesVendidas }
}

// agrupar por categoría con groupBy para mostrar tendencias de venta
val resumenPorCategoria = listaRankingBase
    .groupBy { it.producto.categoria }
    .map { (categoria, itemsCategoria) ->
        val cantidades = itemsCategoria.map { it.unidadesVendidas }
        val montos = itemsCategoria.map { it.montoTotalVentas }

        val sumaUnidades = if (cantidades.isEmpty()) 0 else cantidades.reduce { acc, act -> acc + act }
        val sumaMontos = if (montos.isEmpty()) 0 else montos.reduce { acc, act -> acc + act }

        ItemResumenCategoria(
            categoria = categoria,
            cantidadProductos = itemsCategoria.size,
            unidadesVendidas = sumaUnidades,
            montoTotalCategoria = sumaMontos
        )
    }
    .sortedByDescending { it.unidadesVendidas }

// totales globales del negocio
val listaUnidadesGlobales = listaRankingBase.map { it.unidadesVendidas }
val totalUnidadesVendidas = if (listaUnidadesGlobales.isEmpty()) {
    0
} else {
    listaUnidadesGlobales.reduce { acc, actual -> acc + actual }
}

val listaIngresosGlobales = listaRankingBase.map { it.montoTotalVentas }
val totalIngresosEstimados = if (listaIngresosGlobales.isEmpty()) {
    0
} else {
    listaIngresosGlobales.reduce { acc, actual -> acc + actual }
}

// animacion gradual de color para el encabezado del filtro activo
val colorEncabezadoRanking by animateColorAsState(
    targetValue = if (mostrarMasVendidos) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    },
    label = "animacionColorFiltroRanking"
)

Scaffold(
    topBar = {
        TopAppBar(
            title = { Text("Ranking y Tendencias") },
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
) { innerPadding ->
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // tarjeta de resumen consolidado de ventas
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Visión Consolidada de Ventas Kaiju",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Unidades totales vendidas: $totalUnidadesVendidas uds.",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Monto total en ventas registradas: $$totalIngresosEstimados CLP",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        // recursos nativos en el Ranking: escaner rapido y compartir reporte consolidado
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BotonConPermisoCamara(
                    textoBoton = "Escanear Etiqueta para ir al Detalle",
                    onPermisoConcedido = { codigoLeido -> onProductoClick(codigoLeido) },
                    onSinPermiso = { }
                )
                BotonCompartirReporteNativo(
                    tituloReporte = "Reporte Consolidado de Ventas Kaiju",
                    contenidoReporte = "Resumen Kaiju:\n" +
                            "- Unidades totales vendidas: $totalUnidadesVendidas uds.\n" +
                            "- Ingresos totales: $$totalIngresosEstimados CLP\n" +
                            "- Categoría líder: ${resumenPorCategoria.firstOrNull()?.categoria ?: "N/A"}",
                    textoBoton = "Exportar / Compartir Reporte de Ventas"
                )
            }
        }

        // controles para alternar entre Mas Vendidos / Menos Vendidos y mostrar categorias
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { mostrarMasVendidos = !mostrarMasVendidos },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (mostrarMasVendidos) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(if (mostrarMasVendidos) "Ver Menos Vendidos" else "Ver Más Vendidos")
                }

                Button(
                    onClick = { mostrarResumenCategorias = !mostrarResumenCategorias },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(if (mostrarResumenCategorias) "Ocultar Categorías" else "Por Categoría")
                }
            }
        }

        // seccion animada de Ventas por Categoría
        item {
            AnimatedVisibility(visible = mostrarResumenCategorias) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Ventas Agrupadas por Categoría",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        resumenPorCategoria.forEach { resumen ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${resumen.categoria} (${resumen.cantidadProductos} productos)",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Ingresos: $${resumen.montoTotalCategoria} CLP",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "${resumen.unidadesVendidas} uds.",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // encabezado dinamico del listado de productos con color animado
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorEncabezadoRanking)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = if (mostrarMasVendidos) {
                                "Ranking: Productos Más Vendidos"
                            } else {
                                "Ranking: Productos Menos Vendidos (Baja Rotación)"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Toca cualquier producto para abrir su ficha e historial.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // lista de productos en el ranking
        items(rankingOrdenado) { itemRanking ->
            TarjetaProductoRanking(
                item = itemRanking,
                onClick = { onProductoClick(itemRanking.producto.codigo) }
            )
        }
    }
}
}

// componente reutilizable que dibuja un producto dentro del ranking de Ventas
@Composable
fun TarjetaProductoRanking(
    item: ItemRankingProducto,
    onClick: () -> Unit
) {
Card(
    modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() },
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    )
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.producto.nombre,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Código: ${item.producto.codigo} • Categoría: ${item.producto.categoria}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Stock disponible hoy: ${item.producto.stockActual} uds.",
                style = MaterialTheme.typography.labelMedium,
                color = if (item.producto.stockActual <= item.producto.stockMinimo) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${item.unidadesVendidas} vendidos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "$${item.montoTotalVentas} CLP",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
}

@Preview(showBackground = true)
@Composable
fun PreviewPantallaRanking() {
    PantallaRanking(
        productos = DatosDePrueba.productosIniciales,
        movimientos = DatosDePrueba.movimientosIniciales
    )
}