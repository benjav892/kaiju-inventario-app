package com.example.kaiju.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.UsuarioResponse
import com.example.kaiju.ui.components.BarraSuperior
import com.example.kaiju.ui.components.BotonCerrarSesion
import com.example.kaiju.viewmodel.UsuariosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaUsuarios(
    viewModel: UsuariosViewModel,
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var usuarioAEliminar by remember { mutableStateOf<UsuarioResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarUsuarios()
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Gestión de Usuarios",
                mostrarBotonAtras = true,
                onNavegarAtras = onVolver,
                acciones = { BotonCerrarSesion(onLogoutClick = onCerrarSesion) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.abrirFormularioCrear() }) {
                Icon(Icons.Filled.Add, contentDescription = "Nuevo usuario")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            state.mensajeExito?.let { msg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(msg, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

            state.error?.let { err ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(err, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }

            if (state.cargando && state.usuarios.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.usuarios, key = { it.id }) { usuario ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${usuario.nombre} ${usuario.apellido}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = usuario.email, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = "Rol: ${usuario.rol}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Row {
                                    IconButton(onClick = { viewModel.abrirFormularioEditar(usuario) }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                                    }
                                    IconButton(onClick = { usuarioAEliminar = usuario }) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo Crear / Editar Usuario
    if (state.mostrarFormulario) {
        var menuRolExpandido by remember { mutableStateOf(false) }
        val esEdicion = state.usuarioEditando != null

        AlertDialog(
            onDismissRequest = { viewModel.cerrarFormulario() },
            title = { Text(if (esEdicion) "Editar Usuario" else "Nuevo Usuario") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.nombre,
                        onValueChange = viewModel::onNombreChange,
                        label = { Text("Nombre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.apellido,
                        onValueChange = viewModel::onApellidoChange,
                        label = { Text("Apellido") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (!esEdicion) {
                        OutlinedTextField(
                            value = state.email,
                            onValueChange = viewModel::onEmailChange,
                            label = { Text("Correo (@duoc.cl / @gmail.com)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = state.password,
                            onValueChange = viewModel::onPasswordChange,
                            label = { Text("Contraseña (mín. 4)") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    OutlinedTextField(
                        value = state.numeroTelefono,
                        onValueChange = viewModel::onTelefonoChange,
                        label = { Text("Teléfono (opcional)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenuBox(
                        expanded = menuRolExpandido,
                        onExpandedChange = { menuRolExpandido = !menuRolExpandido }
                    ) {
                        OutlinedTextField(
                            value = state.rolSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Rol") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuRolExpandido) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = menuRolExpandido,
                            onDismissRequest = { menuRolExpandido = false }
                        ) {
                            viewModel.rolesDisponibles.forEach { rol ->
                                DropdownMenuItem(
                                    text = { Text(rol) },
                                    onClick = {
                                        viewModel.onRolChange(rol)
                                        menuRolExpandido = false
                                    }
                                )
                            }
                        }
                    }

                    state.errorFormulario?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.guardarUsuario() }, enabled = !state.cargando) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cerrarFormulario() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Confirmación de eliminación
    usuarioAEliminar?.let { usuario ->
        AlertDialog(
            onDismissRequest = { usuarioAEliminar = null },
            title = { Text("Eliminar Usuario") },
            text = { Text("¿Seguro que deseas eliminar a ${usuario.nombre} ${usuario.apellido}?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.eliminarUsuario(usuario.id)
                    usuarioAEliminar = null
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { usuarioAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}