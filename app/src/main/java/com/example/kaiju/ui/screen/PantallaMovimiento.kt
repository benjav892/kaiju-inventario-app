package com.example.kaiju.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.viewmodel.InventarioViewModel

/**
 * Pantalla "Registrar Movimiento": Formulario 100% declarativo.
 *
 * Lee el estado desde [viewModel.estadoFormularioMovimiento] y los productos desde [viewModel.productos].
 * Delega toda la lógica de validaciones y almacenamiento al [InventarioViewModel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMovimiento(
    viewModel: InventarioViewModel,
    tiposPermitidos: List<TipoMovimiento>,
    usuarioNombre: String,
    onVolver: () -> Unit
) {
    val estado by viewModel.estadoFormularioMovimiento.collectAsState()
    val productos by viewModel.productos.collectAsState()

    var dropdownProductoExpandido by remember { mutableStateOf(false) }
    var dropdownTipoExpandido by remember { mutableStateOf(false) }

    val productoSeleccionado = productos.find { it.codigo == estado.codigoProducto }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Movimiento") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Registro de Entrada, Salida o Ajuste",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            // 1. SELECTOR DE PRODUCTO
            ExposedDropdownMenuBox(
                expanded = dropdownProductoExpandido,
                onExpandedChange = { dropdownProductoExpandido = !dropdownProductoExpandido }
            ) {
                OutlinedTextField(
                    value = productoSeleccionado?.let { "${it.nombre} — Stock: ${it.stockActual}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Producto afectado") },
                    placeholder = { Text("Selecciona un producto") },
                    isError = estado.errores.errorProducto != null,
                    supportingText = {
                        estado.errores.errorProducto?.let { Text(it) } ?: Text("Obligatorio elegir uno.")
                    },
                    leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (estado.errores.errorProducto != null) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownProductoExpandido)
                        }
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = dropdownProductoExpandido,
                    onDismissRequest = { dropdownProductoExpandido = false }
                ) {
                    productos.forEach { producto ->
                        DropdownMenuItem(
                            text = { Text("${producto.nombre} — Disponibles: ${producto.stockActual}") },
                            onClick = {
                                viewModel.actualizarCodigoProductoMovimiento(producto.codigo)
                                dropdownProductoExpandido = false
                            }
                        )
                    }
                }
            }

            // 2. SELECTOR DE TIPO DE MOVIMIENTO
            ExposedDropdownMenuBox(
                expanded = dropdownTipoExpandido,
                onExpandedChange = { dropdownTipoExpandido = !dropdownTipoExpandido }
            ) {
                OutlinedTextField(
                    value = estado.tipo.name.replace("_", " "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de movimiento") },
                    isError = estado.errores.errorTipo != null,
                    supportingText = {
                        estado.errores.errorTipo?.let { Text(it) } ?: Text("Obligatorio elegir uno.")
                    },
                    leadingIcon = { Icon(Icons.Default.List, contentDescription = null) },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (estado.errores.errorTipo != null) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownTipoExpandido)
                        }
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = dropdownTipoExpandido,
                    onDismissRequest = { dropdownTipoExpandido = false }
                ) {
                    tiposPermitidos.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo.name.replace("_", " ")) },
                            onClick = {
                                viewModel.actualizarTipoMovimiento(tipo)
                                dropdownTipoExpandido = false
                            }
                        )
                    }
                }
            }

            // 3. CAMPO CANTIDAD
            OutlinedTextField(
                value = estado.cantidad,
                onValueChange = { nuevoTexto ->
                    if (nuevoTexto.all { it.isDigit() }) {
                        viewModel.actualizarCantidadMovimiento(nuevoTexto)
                    }
                },
                label = { Text("Cantidad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = estado.errores.errorCantidad != null,
                supportingText = {
                    estado.errores.errorCantidad?.let { Text(it) }
                        ?: Text("Número entero mayor que cero (RN-01).")
                },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                trailingIcon = {
                    if (estado.errores.errorCantidad != null) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // 4. CAMPO MOTIVO
            OutlinedTextField(
                value = estado.motivo,
                onValueChange = viewModel::actualizarMotivoMovimiento,
                label = { Text("Motivo") },
                singleLine = true,
                isError = estado.errores.errorMotivo != null,
                supportingText = {
                    estado.errores.errorMotivo?.let { Text(it) }
                        ?: Text("Obligatorio si el tipo es Ajuste (RN-04).")
                },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                trailingIcon = {
                    if (estado.errores.errorMotivo != null) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (viewModel.guardarMovimiento(usuario = usuarioNombre)) {
                        onVolver()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Movimiento")
            }
        }
    }
}