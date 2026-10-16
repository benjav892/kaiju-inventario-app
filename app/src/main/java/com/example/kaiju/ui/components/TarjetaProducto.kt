package com.example.kaiju.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.Producto

@Composable
fun TarjetaProducto(producto: Producto) {
    val esStockBajo = producto.stockActual <= producto.stockMinimo

    val colorStock by animateColorAsState(
        targetValue = if (esStockBajo) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.primary,
        animationSpec = tween(durationMillis = 500)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        border = if (esStockBajo) BorderStroke(1.dp, MaterialTheme.colorScheme.error) else null
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nombre: ${producto.nombre}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                // Marca animada de Stock Bajo
                AnimatedVisibility(
                    visible = esStockBajo,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text("¡Stock Bajo!") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            labelColor = MaterialTheme.colorScheme.onErrorContainer,
                            leadingIconContentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    )
                }
            }

            Text(text = "Categoría: ${producto.categoria}")
            Text(text = "Precio: $${producto.precio}")
            Text(
                text = "Stock: ${producto.stockActual} (Min: ${producto.stockMinimo})",
                color = colorStock,
                fontWeight = if (esStockBajo) FontWeight.Bold else FontWeight.Normal
            )
            //BotonMarcar()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTarjetaProducto() {
    TarjetaProducto(
        producto = Producto(
            codigo = "kai-400",
            nombre = "Producto1",
            descripcion = "El primer producto",
            categoria = "Categoría1",
            tipo = "Tipo1",
            precio = 15990,
            stockActual = 2,
            stockMinimo = 5,
            detalle = null
        )
    )
}
