package com.example.kaiju.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.kaiju.ui.theme.KaijuTheme

@Composable
fun BotonCerrarSesion(
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onLogoutClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = "Cerrar Sesión",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun PreviewBotonCerrarSesion() {
    KaijuTheme {
        BotonCerrarSesion(onLogoutClick = {})
    }
}