package com.example.kaiju.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun IndicadorStock(
    stockActual: Int,
    stockMinimo: Int,
    modifier: Modifier = Modifier
) {
    val enAlerta = stockActual <= stockMinimo
    val colorFondo = if (enAlerta) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
    val colorTexto = if (enAlerta) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
    val etiqueta = if (enAlerta) "STOCK BAJO" else "NORMAL"

    Text(
        text = etiqueta,
        color = colorTexto,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(colorFondo)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}