package com.example.kaiju.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.Rol
import com.example.kaiju.model.Usuario
import com.example.kaiju.viewmodel.SesionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogin(
    sesionViewModel: SesionViewModel,
    onLoginExitoso: () -> Unit
) {
    // suscripción reactiva al StateFlow del ViewModel
    val estadoFormulario by sesionViewModel.estadoFormulario.collectAsState()
    val usuarios = sesionViewModel.usuariosDisponibles

    var menuExpandido by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kaiju - Control de Inventario") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Encabezado de bienvenida usando tipografía y colores centralizados en MaterialTheme
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Selecciona tu perfil de trabajo para acceder a las funciones de inventario según tu rol (RF-01).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // campo validado con OutlinedTextField + DropdownMenu
            // se utiliza DropdownMenu junto a un OutlinedTextField
            // de solo lectura para elegir un usuario de una lista fija, evitando errores de tipeo manual
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = estadoFormulario.usuarioSeleccionado?.let {
                        "${it.nombre} - ${formatearRol(it.rol)}"
                    } ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Usuario de prueba") },
                    placeholder = { Text("Toca para elegir un perfil...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Ícono de usuario"
                        )
                    },
                    trailingIcon = {
                        if (estadoFormulario.errores.errorUsuario != null) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error de validación",
                                tint = MaterialTheme.colorScheme.error
                            )
                        } else {
                            IconButton(onClick = { menuExpandido = !menuExpandido }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Desplegar lista de usuarios"
                                )
                            }
                        }
                    },
                    isError = estadoFormulario.errores.errorUsuario != null,
                    supportingText = {
                        estadoFormulario.errores.errorUsuario?.let { mensajeError ->
                            Text(text = mensajeError)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { menuExpandido = true }
                )

                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    usuarios.forEach { usuario ->
                        DropdownMenuItem(
                            text = {
                                Text("${usuario.nombre} (${formatearRol(usuario.rol)})")
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                sesionViewModel.seleccionarUsuario(usuario)
                                menuExpandido = false
                            }
                        )
                    }
                }
            }

            Text(
                text = "O selecciona directamente una tarjeta de perfil:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            // tarjetas de selección rápida con RadioButton y animación de color
            // RadioButton dentro de cada Card para representar
            // una selección excluyente (un único usuario activo a la vez).
            usuarios.forEach { usuario ->
                TarjetaSeleccionUsuario(
                    usuario = usuario,
                    seleccionado = estadoFormulario.usuarioSeleccionado?.id == usuario.id,
                    onSeleccionar = { sesionViewModel.seleccionarUsuario(usuario) }
                )
            }

            // animación con proposito: AnimatedVisibility muestra de forma fluida los permisos asociados al perfil elegido.
            AnimatedVisibility(visible = estadoFormulario.usuarioSeleccionado != null) {
                estadoFormulario.usuarioSeleccionado?.let { usuario ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Permisos del perfil",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Column {
                                Text(
                                    text = "Permisos activos para ${formatearRol(usuario.rol)}:",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = descripcionPermisosPorRol(usuario.rol),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // botón de ingreso: delega la validación al ViewModel
            Button(
                onClick = {
                    if (sesionViewModel.iniciarSesion()) {
                        onLoginExitoso()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = if (estadoFormulario.usuarioSeleccionado != null) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.Lock
                    },
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Ingresar a Kaiju")
            }
        }
    }
}

// Componente reutilizable de tarjeta de usuario con transicion gradual de color
@Composable
fun TarjetaSeleccionUsuario(
    usuario: Usuario,
    seleccionado: Boolean,
    onSeleccionar: () -> Unit
) {
    // Animación gradual de color según si el perfil está seleccionado o no
    val colorFondo by animateColorAsState(
        targetValue = if (seleccionado) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        label = "animacionColorTarjetaUsuario"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSeleccionar() },
        colors = CardDefaults.cardColors(containerColor = colorFondo)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = usuario.nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Rol: ${formatearRol(usuario.rol)} • ID: ${usuario.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            RadioButton(
                selected = seleccionado,
                onClick = onSeleccionar
            )
        }
    }
}

// funcion auxiliar de presentación para mostrar un texto legible del enum Rol
private fun formatearRol(rol: Rol): String {
    return when (rol) {
        Rol.ADMINISTRADOR -> "Administrador"
        Rol.VENDEDOR -> "Personal de Venta"
        Rol.ENCARGADO_INVENTARIO -> "Encargado de Inventario"
    }
}

// funcion auxiliar que resume qué puede hacer cada perfil en la app
private fun descripcionPermisosPorRol(rol: Rol): String {
    return when (rol) {
        Rol.ADMINISTRADOR -> "Crear y editar productos, registrar entradas/ajustes y revisar alertas de stock bajo."
        Rol.ENCARGADO_INVENTARIO -> "Registrar entradas, salidas y ajustes de stock y monitorear alertas de reposición."
        Rol.VENDEDOR -> "Consultar catálogo y disponibilidad y registrar salidas por venta."
    }
}