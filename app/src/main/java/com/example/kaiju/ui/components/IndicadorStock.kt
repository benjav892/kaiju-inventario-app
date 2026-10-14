package com.example.kaiju.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun IndicadorStock(stockActual: Int, stockMinimo: Int) {
    val esStockBajo = stockActual <= stockMinimo

    val colorFondo = if (esStockBajo) Color(0xFFE53935) else Color(0xFF4CAF50)
    val texto = if (esStockBajo) "⚠️ Stock Bajo: $stockActual" else "Stock: $stockActual"

    Text(
        text = texto,
        color = Color.White,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .background(color = colorFondo, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}