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

//recurso nativo 2a: Botón para compartir reportes de stock o ventas con otras apps mediante
// Intent.ACTION_SEND y confirmación háptica con LocalHapticFeedback

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
            // confirmación táctil al ejecutar el recurso nativo
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

// recurso nativo 2b: selector seguro de fotos de la galería con PickVisualMedia sin necesidad
// de solicitar permisos de almacenamiento completo
@Composable
fun SelectorImagenProductoNativo(
    onImagenSeleccionada: (Uri) -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    var uriSeleccionada by remember { mutableStateOf<Uri?>(null) }
    var avisoAlternativa by remember { mutableStateOf(false) }

    val selectorFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        // uri es null si el usuario cerró el selector sin elegir
        if (uri != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            uriSeleccionada = uri
            avisoAlternativa = false
            onImagenSeleccionada(uri)
        } else {
            avisoAlternativa = true
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
            Text("Elegir Foto de Referencia (Galería)")
        }

        AnimatedVisibility(visible = uriSeleccionada != null || avisoAlternativa) {
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
                        text = uriSeleccionada?.let {
                            "Imagen vinculada desde galería: ${it.lastPathSegment ?: "foto seleccionada"}"
                        } ?: "No se seleccionó imagen. El producto mantiene su ficha estándar sin foto.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// funcion auxiliar que invoca la hoja de compartir nativa de Android (Intent.ACTION_SEND)
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