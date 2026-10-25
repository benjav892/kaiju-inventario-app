package com.example.kaiju.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.ui.theme.KaijuTheme

/**
 * Tarjeta individual para presentar un registro del historial de movimientos.
 */
@Composable
fun TarjetaMovimiento(
    movimiento: Movimiento,
    producto: Producto?,
    modifier: Modifier = Modifier
) {
    val esEntrada = movimiento.tipo == TipoMovimiento.ENTRADA
    val colorCantidad = if (esEntrada) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        // Borde fino de color rojo suave utilizando la paleta del tema
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Encabezado: Tipo de movimiento y fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = movimiento.tipo.name.replace("_", " "),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = movimiento.fecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Datos del producto
            Text(
                text = producto?.nombre ?: "Producto desconocido",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Código: ${movimiento.codigoProducto}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            // Cantidad y Usuario
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cantidad: ${if (esEntrada) "+${movimiento.cantidad}" else "-${movimiento.cantidad}"}",
                    fontWeight = FontWeight.Bold,
                    color = colorCantidad,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Por: ${movimiento.usuario}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Motivo
            if (movimiento.motivo.isNotBlank()) {
                Text(
                    text = "Motivo: ${movimiento.motivo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true,backgroundColor = 0xFF0B0F19)
@Composable
fun PreviewTarjetaMovimiento() {
    KaijuTheme {
        TarjetaMovimiento(
            movimiento = Movimiento(
                id = 6,
                codigoProducto = "KJ-CHA-002",
                tipo = TipoMovimiento.AJUSTE,
                cantidad = 1,
                motivo = "Diferencia detectada en conteo físico de bodega",
                fecha = "2026-10-20 10:15",
                usuario = "Carla Méndez (Admin)"
            ),
            producto = Producto(
                codigo = "KJ-CHA-002",
                nombre = "Chaqueta Kaiju Leather",
                descripcion = "Chaqueta de cuero sintético",
                categoria = "Chaquetas",
                tipo = "Ropa",
                precio = 15990,
                stockActual = 2,
                stockMinimo = 5,
                detalle = ""
            )
        )
    }
}