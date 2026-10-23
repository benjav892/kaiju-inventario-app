package com.example.kaiju.ui.components

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.example.kaiju.repository.DatosDePrueba

//recurso nativo 1: escáner / verificador por cámara con flujo completo de permisos
//en tiempo de ejecución y reacción al ciclo de vida.

@Composable
fun BotonConPermisoCamara(
    textoBoton: String = "Escanear Código QR / Barras",
    onPermisoConcedido: (String) -> Unit,
    onSinPermiso: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val haptic = LocalHapticFeedback.current

    var mostrarExplicacion by remember { mutableStateOf(false) }
    var mostrarSelectorSimulado by remember { mutableStateOf(false) }
    var mensajeEstadoPermiso by remember { mutableStateOf<String?>(null) }

    // verificar permiso al volver a primer plano en onResume
    // LifecycleResumeEffect vuelve a comprobar el estado del permiso cada vez que la Activity
    // regresa a onResume (ej. tras volver de Ajustes).
    var tienePermisoCamara by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    LifecycleResumeEffect(Unit) {
        tienePermisoCamara = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        onPauseOrDispose { }
    }

    // launcher que recibe la respuesta del dialogo oficial de Android
    val launcherPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        tienePermisoCamara = concedido
        if (concedido) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            mensajeEstadoPermiso = "Permiso de cámara concedido. Selecciona el código detectado:"
            mostrarSelectorSimulado = true
        } else {
            mensajeEstadoPermiso = "Permiso denegado. Puedes continuar usando el ingreso manual sin bloqueo."
            onSinPermiso()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {
                val yaConcedido = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                when {
                    // camino A: ya tiene permiso concedido -> se usa directo
                    yaConcedido -> {
                        tienePermisoCamara = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        mensajeEstadoPermiso = "Cámara activa y autorizada para lectura de etiquetas Kaiju."
                        mostrarSelectorSimulado = true
                    }
                    // camino B: el usuario rechazó antes -> Android sugiere mostrar explicacion en contexto
                    activity != null && ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.CAMERA
                    ) -> {
                        mostrarExplicacion = true
                    }
                    // camino C: se solicita el permiso con el dialogo del sistema
                    else -> {
                        launcherPermiso.launch(Manifest.permission.CAMERA)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Escanear código con cámara",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(textoBoton)
        }

        // retroalimentación visual animada del estado del permiso o alternativa
        AnimatedVisibility(visible = mensajeEstadoPermiso != null) {
            mensajeEstadoPermiso?.let { mensaje ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (tienePermisoCamara) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (tienePermisoCamara) {
                                Icons.Default.CheckCircle
                            } else {
                                Icons.Default.Warning
                            },
                            contentDescription = null,
                            tint = if (tienePermisoCamara) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                        )
                        Text(
                            text = mensaje,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // simulador seguro de lectura de etiqueta para pruebas en emulador sin capturar personas reales
        AnimatedVisibility(visible = mostrarSelectorSimulado && tienePermisoCamara) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Lector de etiquetas Kaiju activo (prueba segura sin capturar personas):",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    DatosDePrueba.productosIniciales.take(3).forEach { prod ->
                        OutlinedButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                mostrarSelectorSimulado = false
                                mensajeEstadoPermiso = "Código leído correctamente: ${prod.codigo} (${prod.nombre})"
                                onPermisoConcedido(prod.codigo)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Simular lectura QR: ${prod.codigo} - ${prod.nombre}")
                        }
                    }
                }
            }
        }
    }

    // explicacion propia con AlertDialog
    if (mostrarExplicacion) {
        AlertDialog(
            onDismissRequest = { mostrarExplicacion = false },
            title = { Text("Usaremos la cámara") },
            text = {
                Text(
                    "La cámara sirve solo para leer el código de barras o QR de la etiqueta del producto en bodega. " +
                            "Por privacidad del caso Kaiju, no guardamos fotografías de personas."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarExplicacion = false
                        launcherPermiso.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text("Continuar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarExplicacion = false
                        mensajeEstadoPermiso = "Permiso rechazado en explicación. Se mantiene el ingreso manual."
                        onSinPermiso()
                    }
                ) {
                    Text("Ahora no")
                }
            }
        )
    }
}