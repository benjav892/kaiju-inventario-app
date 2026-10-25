package com.example.kaiju.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kaiju.ui.theme.KaijuTheme
@Composable
fun BotonFlotanteAgregar(
    onClick: () -> Unit,
    descripcion: String = "Agregar"
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = descripcion,
            modifier = Modifier.size(50.dp)
        )
    }
}
@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun BotonFlotanteAgregarPreview() {
    KaijuTheme {
        BotonFlotanteAgregar(
            onClick = {},
            descripcion = "Nuevo Movimiento"
        )
    }
}