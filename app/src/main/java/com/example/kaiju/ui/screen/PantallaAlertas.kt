package com.example.kaiju.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.Producto
import com.example.kaiju.ui.components.TarjetaProductoAlerta
import androidx.compose.ui.tooling.preview.Preview
import com.example.kaiju.ui.components.BarraSuperior
import com.example.kaiju.ui.components.BotonCerrarSesion
import com.example.kaiju.ui.theme.KaijuTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAlertas(
    productosEnAlerta: List<Producto>,
    onProductoClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Alertas de Reposición",
                mostrarBotonAtras = true,
                onNavegarAtras = onVolver,
                acciones = {
                    BotonCerrarSesion(onLogoutClick = onCerrarSesion)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 1.dp)
            ) {
                Text(
                    text = "Productos con Stock Bajo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "${productosEnAlerta.size} productos.",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (productosEnAlerta.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay productos en alerta de stock bajo actualmente.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(productosEnAlerta, key = { it.codigo }) { producto ->
                        TarjetaProductoAlerta(
                            producto = producto,
                            onClick = { onProductoClick(producto.codigo) }
                        )
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun PantallaAlertasPreview() {
    val productosDePrueba = listOf(
        Producto(
            codigo = "kai-400",
            nombre = "Producto1",
            descripcion = "El primer producto",
            categoria = "Categoría1",
            tipo = "Tipo1",
            precio = 15990,
            stockActual = 2,
            stockMinimo = 5,
            detalle = ""
        ),
        Producto(
            codigo = "kai-402",
            nombre = "Producto1",
            descripcion = "El primer producto",
            categoria = "Categoría1",
            tipo = "Tipo1",
            precio = 15990,
            stockActual = 2,
            stockMinimo = 5,
            detalle = ""
        )
    )

    KaijuTheme {
        PantallaAlertas(
            productosEnAlerta = productosDePrueba,
            onProductoClick = {},
            onVolver = {}
        )
    }
}