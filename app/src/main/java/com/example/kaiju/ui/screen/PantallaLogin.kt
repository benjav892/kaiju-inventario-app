package com.example.kaiju.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kaiju.viewmodel.SesionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogin(
    sesionViewModel: SesionViewModel,
    onLoginExitoso: () -> Unit
) {
    val usuarios = sesionViewModel.usuariosPrueba
    val usuarioSeleccionadoId by sesionViewModel.usuarioSeleccionadoId.collectAsState()
    val errorLogin by sesionViewModel.errorLogin.collectAsState()

    var dropdownExpandido by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Iniciar Sesión - Kaiju") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenido a Kaiju Inventory",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Seleccione un perfil de prueba para continuar:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            ExposedDropdownMenuBox(
                expanded = dropdownExpandido,
                onExpandedChange = { dropdownExpandido = !dropdownExpandido }
            ) {
                val usuarioActual = usuarios.find { it.id == usuarioSeleccionadoId }

                OutlinedTextField(
                    value = usuarioActual?.let { "${it.nombre} (${it.rol})" } ?: "Seleccionar perfil...",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Usuario de Prueba") },
                    isError = errorLogin != null,
                    supportingText = { errorLogin?.let { Text(it) } },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpandido) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = dropdownExpandido,
                    onDismissRequest = { dropdownExpandido = false }
                ) {
                    usuarios.forEach { usuario ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(text = usuario.nombre, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "Rol: ${usuario.rol}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            onClick = {
                                sesionViewModel.seleccionarUsuarioLogin(usuario.id)
                                dropdownExpandido = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (sesionViewModel.iniciarSesion()) {
                        onLoginExitoso()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar a la App")
            }
        }
    }
}