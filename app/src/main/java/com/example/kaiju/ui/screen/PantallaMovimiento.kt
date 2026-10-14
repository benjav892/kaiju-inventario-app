package com.example.kaiju.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMovimiento(
    viewModel: InventarioViewModel,
    usuarioActual: String = "Vendedor"
) {
    val estadoFormulario by viewModel.estadoFormularioMovimiento.collectAsState()
    val productos by viewModel.productos.collectAsState()

    var dropdownExpandido by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Registrar Movimiento") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 1. Input de Producto (Texto editable con sugerencias dinámicas)
            val textoIngresado = estadoFormulario.codigoProducto

            val productosFiltrados = remember(textoIngresado, productos) {
                if (textoIngresado.isBlank()) {
                    productos
                } else {
                    productos.filter { producto ->
                        producto.codigo.contains(textoIngresado, ignoreCase = true) ||
                                producto.nombre.contains(textoIngresado, ignoreCase = true)
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = dropdownExpandido && productosFiltrados.isNotEmpty(),
                onExpandedChange = { dropdownExpandido = it }
            ) {
                OutlinedTextField(
                    value = estadoFormulario.codigoProducto,
                    onValueChange = { texto ->
                        viewModel.actualizarCodigoProductoMovimiento(texto)
                        dropdownExpandido = true
                    },
                    label = { Text("Código o Nombre del producto") },
                    placeholder = { Text("Ej: PROD01 o Polera Kaiju") },
                    isError = estadoFormulario.errores.errorProducto != null,
                    supportingText = { estadoFormulario.errores.errorProducto?.let { Text(it) } },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpandido) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = dropdownExpandido && productosFiltrados.isNotEmpty(),
                    onDismissRequest = { dropdownExpandido = false }
                ) {
                    productosFiltrados.forEach { producto ->
                        DropdownMenuItem(
                            text = { Text("${producto.nombre} (${producto.codigo}) - Stock: ${producto.stockActual}") },
                            onClick = {
                                viewModel.actualizarCodigoProductoMovimiento(producto.codigo)
                                dropdownExpandido = false
                            }
                        )
                    }
                }
            }

            // 2. Tipo de Movimiento (RadioButtons)
            Text("Tipo de Movimiento:", style = MaterialTheme.typography.titleMedium)
            Column {
                TipoMovimiento.values().forEach { tipo ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = estadoFormulario.tipo == tipo,
                            onClick = { viewModel.actualizarTipoMovimiento(tipo) }
                        )
                        Text(text = tipo.name.replace("_", " "))
                    }
                }
                estadoFormulario.errores.errorTipo?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // 3. Cantidad
            OutlinedTextField(
                value = estadoFormulario.cantidad,
                onValueChange = { viewModel.actualizarCantidadMovimiento(it) },
                label = { Text("Cantidad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = estadoFormulario.errores.errorCantidad != null,
                supportingText = { estadoFormulario.errores.errorCantidad?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            // 4. Motivo
            OutlinedTextField(
                value = estadoFormulario.motivo,
                onValueChange = { viewModel.actualizarMotivoMovimiento(it) },
                label = {
                    Text(if (estadoFormulario.tipo == TipoMovimiento.AJUSTE) "Motivo (Obligatorio)" else "Motivo (Opcional)")
                },
                isError = estadoFormulario.errores.errorMotivo != null,
                supportingText = { estadoFormulario.errores.errorMotivo?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            // 5. Botón Guardar
            Button(
                onClick = { viewModel.guardarMovimiento(usuarioActual) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Movimiento")
            }
        }
    }
}