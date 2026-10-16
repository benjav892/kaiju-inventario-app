package com.example.kaiju.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kaiju.viewmodel.InventarioViewModel

@Composable
fun PantallaFormularioProducto(
    viewModel: InventarioViewModel = viewModel(),
    onProductoGuardado: () -> Unit = {}
) {
    val estado by viewModel.estadoFormulario.collectAsState()

    Scaffold(
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Código
            OutlinedTextField(
                value = estado.codigo,
                onValueChange = viewModel::actualizarCodigo,
                label = { Text("codigo") },
                isError = estado.errores.errorCodigo != null,
                supportingText = { estado.errores.errorCodigo?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = estado.nombre,
                onValueChange = viewModel::actualizarNombre,
                label = { Text("nombre") },
                isError = estado.errores.errorNombre != null,
                supportingText = { estado.errores.errorNombre?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 3. Descripción
            OutlinedTextField(
                value = estado.descripcion,
                onValueChange = viewModel::actualizarDescripcion,
                label = { Text("Descripción") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            // 4. Categoría
            OutlinedTextField(
                value = estado.categoria,
                onValueChange = viewModel::actualizarCategoria,
                label = { Text("Categoría") },
                isError = estado.errores.errorCategoria != null,
                supportingText = { estado.errores.errorCategoria?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 5. Tipo
            OutlinedTextField(
                value = estado.tipo,
                onValueChange = viewModel::actualizarTipo,
                label = { Text("Tipo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 6. Precio
            OutlinedTextField(
                value = estado.precio,
                onValueChange = viewModel::actualizarPrecio,
                label = { Text("Precio") },
                isError = estado.errores.errorPrecio != null,
                supportingText = { estado.errores.errorPrecio?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 7. Stock Actual
            OutlinedTextField(
                value = estado.stockActual,
                onValueChange = viewModel::actualizarStockActual,
                label = { Text("Stock Actual") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 8. Stock Mínimo
            OutlinedTextField(
                value = estado.stockMinimo,
                onValueChange = viewModel::actualizarStockMinimo,
                label = { Text("Stock Mínimo") },
                isError = estado.errores.errorStockMinimo != null,
                supportingText = { estado.errores.errorStockMinimo?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 9. Detalle
            OutlinedTextField(
                value = estado.detalle,
                onValueChange = viewModel::actualizarDetalle,
                label = { Text("Detalle") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            // Botón Guardar
            Button(
                onClick = {
                    val exito = viewModel.guardarProducto()
                    if (exito) {
                        onProductoGuardado()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Guardar Producto")
            }
        }
    }
}

