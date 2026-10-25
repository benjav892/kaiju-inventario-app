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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.tooling.preview.Preview
import com.example.kaiju.ui.theme.KaijuTheme
import androidx.compose.foundation.layout.Column

@Composable
fun IndicadorStock(
    stockActual: Int,
    stockMinimo: Int,
    modifier: Modifier = Modifier
) {
    val enAlerta = stockActual <= stockMinimo

    AnimatedVisibility(
        visible = enAlerta,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Text(
            text = "¡STOCK BAJO!",
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun PreviewIndicadorStock() {
    KaijuTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Caso 1: Stock Bajo (Se muestra)
            IndicadorStock(stockActual = 2, stockMinimo = 5)

            // Caso 2: Stock Normal (No se renderiza)
            IndicadorStock(stockActual = 10, stockMinimo = 5)
        }
    }
}