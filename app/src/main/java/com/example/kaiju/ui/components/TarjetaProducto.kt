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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.Producto
import com.example.kaiju.ui.theme.KaijuTheme

@Composable
fun TarjetaProducto(producto: Producto, onClick: () -> Unit = {}) {
    val esStockBajo = producto.stockActual <= producto.stockMinimo

    val colorStock by animateColorAsState(
        targetValue = if (esStockBajo) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.primary,
        animationSpec = tween(durationMillis = 500)
    )
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (esStockBajo) BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )

                // Marca animada de Stock Bajo
                IndicadorStock(
                    stockActual = producto.stockActual,
                    stockMinimo = producto.stockMinimo
                )
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

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun PreviewTarjetaProducto() {
    KaijuTheme {
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
                detalle = ""
            )
        )
    }
}
