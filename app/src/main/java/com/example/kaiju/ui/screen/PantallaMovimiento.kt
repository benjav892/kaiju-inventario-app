package com.example.kaiju.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.FormularioMovimientoEstado
import com.example.kaiju.model.Producto
import com.example.kaiju.model.TipoMovimiento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMovimiento(
    estado: FormularioMovimientoEstado,
    productos: List<Producto>,
    tiposPermitidos: List<TipoMovimiento>,
    onCodigoProductoChange: (String) -> Unit,
    onTipoChange: (TipoMovimiento) -> Unit,
    onCantidadChange: (String) -> Unit,
    onMotivoChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    var desplegarProducto by remember { mutableStateOf(false) }
    var desplegarTipo by remember { mutableStateOf(false) }

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
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Registro de Entrada, Salida o Ajuste",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Selector de Producto
            ExposedDropdownMenuBox(
                expanded = desplegarProducto,
                onExpandedChange = { desplegarProducto = !desplegarProducto }
            ) {
                OutlinedTextField(
                    value = productoSeleccionado?.let { "${it.nombre} (${it.codigo})" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Producto afectado") },
                    leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = desplegarProducto) },
                    isError = estado.errores.errorProducto != null,
                    supportingText = { estado.errores.errorProducto?.let { Text(it) } },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = desplegarProducto,
                    onDismissRequest = { desplegarProducto = false }
                ) {
                    productos.forEach { prod ->
                        DropdownMenuItem(
                            text = { Text("${prod.nombre} — Disponibles: ${prod.stockActual}") },
                            onClick = {
                                onCodigoProductoChange(prod.codigo)
                                desplegarProducto = false
                            }
                        )
                    }
                }
            }

            // Selector de Tipo de Movimiento
            ExposedDropdownMenuBox(
                expanded = desplegarTipo,
                onExpandedChange = { desplegarTipo = !desplegarTipo }
            ) {
                OutlinedTextField(
                    value = when (estado.tipo) {
                        TipoMovimiento.ENTRADA -> "Entrada de Mercadería"
                        TipoMovimiento.SALIDA_VENTA -> "Salida por Venta"
                        TipoMovimiento.SALIDA_CONSUMO -> "Salida por Consumo Interno"
                        TipoMovimiento.AJUSTE -> "Ajuste de Inventario"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de Movimiento") },
                    leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = desplegarTipo) },
                    isError = estado.errores.errorTipo != null,
                    supportingText = { estado.errores.errorTipo?.let { Text(it) } },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = desplegarTipo,
                    onDismissRequest = { desplegarTipo = false }
                ) {
                    tiposPermitidos.forEach { tipo ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    when (tipo) {
                                        TipoMovimiento.ENTRADA -> "Entrada (Recepción)"
                                        TipoMovimiento.SALIDA_VENTA -> "Salida (Venta)"
                                        TipoMovimiento.SALIDA_CONSUMO -> "Salida (Consumo interno)"
                                        TipoMovimiento.AJUSTE -> "Ajuste de Inventario"
                                    }
                                )
                            },
                            onClick = {
                                onTipoChange(tipo)
                                desplegarTipo = false
                            }
                        )
                    }
                }
            }

            // Campo Cantidad
            OutlinedTextField(
                value = estado.cantidad,
                onValueChange = onCantidadChange,
                label = { Text("Cantidad") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                isError = estado.errores.errorCantidad != null,
                supportingText = { estado.errores.errorCantidad?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo Motivo
            OutlinedTextField(
                value = estado.motivo,
                onValueChange = onMotivoChange,
                label = { Text(if (estado.tipo == TipoMovimiento.AJUSTE) "Motivo del Ajuste (Obligatorio)" else "Motivo / Comentario (Opcional)") },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                isError = estado.errores.errorMotivo != null,
                supportingText = { estado.errores.errorMotivo?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Movimiento")
            }
        }
    }
}