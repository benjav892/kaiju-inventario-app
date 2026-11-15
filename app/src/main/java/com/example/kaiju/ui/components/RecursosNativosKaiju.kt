package com.example.kaiju.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BotonCompartirReporteNativo(
    tituloReporte: String,
    contenidoReporte: String,
    textoBoton: String = "Compartir Reporte"
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    OutlinedButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            compartirTextoNativo(
                context = context,
                asunto = tituloReporte,
                cuerpo = contenidoReporte
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Compartir con otra aplicación",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(textoBoton)
    }
}

@Composable
fun SelectorImagenProductoNativo(
    imagenActualUri: String? = null,
    onImagenSeleccionada: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var avisoSinSeleccion by remember { mutableStateOf(false) }

    val selectorFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            // solicita permiso persistente de lectura sobre el Uri elegido para que sobreviva al reiniciar la app
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
            }

            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            avisoSinSeleccion = false
            onImagenSeleccionada(uri.toString())
        } else {
            avisoSinSeleccion = true
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        OutlinedButton(
            onClick = {
                selectorFoto.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = "Elegir imagen del producto",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                if (imagenActualUri.isNullOrBlank()) {
                    "Elegir Foto de Referencia (Galería)"
                } else {
                    "Cambiar Foto del Producto (Galería)"
                }
            )
        }

        AnimatedVisibility(visible = !imagenActualUri.isNullOrBlank() || avisoSinSeleccion) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (!imagenActualUri.isNullOrBlank()) {
                            "Foto guardada en el producto."
                        } else {
                            "No se seleccionó una foto nueva."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun compartirTextoNativo(
    context: Context,
    asunto: String,
    cuerpo: String
) {
    val intentEnviar = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, asunto)
        putExtra(Intent.EXTRA_TEXT, cuerpo)
    }
    val chooser = Intent.createChooser(intentEnviar, "Compartir reporte Kaiju mediante:")
    context.startActivity(chooser)
}