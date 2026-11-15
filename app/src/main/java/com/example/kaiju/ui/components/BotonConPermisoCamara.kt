package com.example.kaiju.ui.components

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.example.kaiju.repository.DatosDePrueba
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay

@Composable
fun BotonConPermisoCamara(
    textoBoton: String = "Escanear Código QR / Barras con Cámara",
    onPermisoConcedido: (String) -> Unit,
    onSinPermiso: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val haptic = LocalHapticFeedback.current

    var mostrarExplicacion by remember { mutableStateOf(false) }
    var mensajeEstadoPermiso by remember { mutableStateOf<String?>(null) }
    var fotoCapturada by remember { mutableStateOf<Bitmap?>(null) }

    // variable reactiva para controlar la navegación segura
    var codigoParaNavegar by remember { mutableStateOf<String?>(null) }

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

    // navegación diferida y codificación de URLs
    LaunchedEffect(codigoParaNavegar) {
        codigoParaNavegar?.let { codigo ->
            delay(400)
            try {
                val codigoSeguro = java.net.URLEncoder.encode(codigo, "UTF-8")
                onPermisoConcedido(codigoSeguro)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al navegar: ${e.message}", Toast.LENGTH_LONG).show()
            }
            codigoParaNavegar = null
        }
    }

    val launcherCamaraReal = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            try {
                // convertir Bitmap de Hardware a Software
                val safeBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true) ?: bitmap
                fotoCapturada = safeBitmap
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                val imagenMlKit = InputImage.fromBitmap(safeBitmap, 0)
                val scanner = BarcodeScanning.getClient()

                scanner.process(imagenMlKit)
                    .addOnSuccessListener { codigos ->
                        val valorLeido = codigos.firstOrNull()?.rawValue?.trim()
                        if (!valorLeido.isNullOrBlank()) {
                            val productoCoincidente = DatosDePrueba.productosIniciales.find {
                                it.codigo.equals(valorLeido, ignoreCase = true)
                            }
                            val codigoDestino = productoCoincidente?.codigo ?: valorLeido
                            mensajeEstadoPermiso = "Código QR/Barras leído: $codigoDestino"

                            codigoParaNavegar = codigoDestino
                        } else {
                            // 100% Real: Si no hay QR en la foto, avisa el error sin simular nada
                            mensajeEstadoPermiso = "No se detectó un código válido en la imagen. Intenta de nuevo."
                        }
                    }
                    .addOnFailureListener {
                        mensajeEstadoPermiso = "Error al escanear la imagen. Intenta de nuevo."
                    }
            } catch (e: Exception) {
                mensajeEstadoPermiso = "Error interno al procesar la foto."
                Toast.makeText(context, "Fallo interno cámara: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            mensajeEstadoPermiso = "Captura cancelada por el usuario."
        }
    }

    val launcherPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        tienePermisoCamara = concedido
        if (concedido) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            mensajeEstadoPermiso = "Permiso concedido. Abriendo cámara del dispositivo..."
            launcherCamaraReal.launch(null)
        } else {
            mensajeEstadoPermiso = "Permiso denegado. Puedes continuar usando el ingreso manual sin bloqueo desde Catálogo."
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
                    yaConcedido -> {
                        tienePermisoCamara = true
                        launcherCamaraReal.launch(null)
                    }
                    activity != null && ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.CAMERA
                    ) -> {
                        mostrarExplicacion = true
                    }
                    else -> {
                        launcherPermiso.launch(Manifest.permission.CAMERA)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Abrir cámara para escanear",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(textoBoton)
        }

        AnimatedVisibility(visible = mensajeEstadoPermiso != null || fotoCapturada != null) {
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    mensajeEstadoPermiso?.let { mensaje ->
                        Row(
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

                    fotoCapturada?.let { bmp ->
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Captura real de cámara",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }

    if (mostrarExplicacion) {
        AlertDialog(
            onDismissRequest = { mostrarExplicacion = false },
            title = { Text("Usaremos la cámara") },
            text = {
                Text(
                    "La cámara se abrirá para capturar y leer el código de barras o QR de la etiqueta del producto. " +
                            "Si rechazas el permiso, podrás seguir buscando productos manualmente desde Catálogo."
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
                        mensajeEstadoPermiso = "Permiso rechazado en explicación. Se mantiene el ingreso manual desde pantalla Catálogo."
                        onSinPermiso()
                    }
                ) {
                    Text("Ahora no")
                }
            }
        )
    }
}