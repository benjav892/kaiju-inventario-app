package com.example.kaiju.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kaiju.model.ErroresProducto
import com.example.kaiju.model.FormularioProductoEstado
import com.example.kaiju.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.listOf


class InventarioViewModel : ViewModel() {

    // estado del formulario
    private val _estadoFormulario = MutableStateFlow(FormularioProductoEstado())
    val estadoFormulario: StateFlow<FormularioProductoEstado> = _estadoFormulario.asStateFlow()

    // lista de productos
    private val _productos = MutableStateFlow<List<Producto>>(listOf(
            Producto(
                codigo = "k1", nombre = "producto1", descripcion = "primer producto", categoria = "categoría1", tipo = "tipo1", precio = 15990, stockActual = 1, stockMinimo = 5, detalle = ""),
    Producto(codigo = "k2", nombre = "producto2", descripcion = "segundo producto", categoria = "categoría1", tipo = "tipo2", precio = 8990, stockActual = 5, stockMinimo = 2, detalle = ""),
    Producto(codigo = "k3", nombre = "producto3", descripcion = "tercer producto", categoria = "categoría2", tipo = "tipo1", precio = 24990, stockActual = 3, stockMinimo = 3, detalle = ""
    )
    ))
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    fun actualizarCodigo(valor: String) = _estadoFormulario.update { it.copy(codigo = valor) }
    fun actualizarNombre(valor: String) = _estadoFormulario.update { it.copy(nombre = valor) }
    fun actualizarDescripcion(valor: String) = _estadoFormulario.update { it.copy(descripcion = valor) }
    fun actualizarCategoria(valor: String) = _estadoFormulario.update { it.copy(categoria = valor) }
    fun actualizarTipo(valor: String) = _estadoFormulario.update { it.copy(tipo = valor) }
    fun actualizarPrecio(valor: String) = _estadoFormulario.update { it.copy(precio = valor) }
    fun actualizarStockActual(valor: String) = _estadoFormulario.update { it.copy(stockActual = valor) }
    fun actualizarStockMinimo(valor: String) = _estadoFormulario.update { it.copy(stockMinimo = valor) }
    fun actualizarDetalle(valor: String) = _estadoFormulario.update { it.copy(detalle = valor) }

    fun validarFormulario(): Boolean {
        val estado = _estadoFormulario.value
        val codigo = estado.codigo.trim()
        val precio = estado.precio.toIntOrNull()
        val stockMinimo = estado.stockMinimo.toIntOrNull()

        val errorCodigo = when {
            codigo.isBlank() -> "El código es obligatorio"
            _productos.value.any { it.codigo == codigo } -> "Ese código ya existe"
            else -> null
        }
        val errorNombre =
            if (estado.nombre.trim().length < 3) "El nombre debe tener al menos 3 caracteres" else null
        val errorCategoria =
            if (estado.categoria.isBlank()) "La categoría es obligatoria" else null
        val errorPrecio = when {
            precio == null -> "El precio debe ser un número entero"
            precio <= 0 -> "El precio debe ser mayor que cero"
            else -> null
        }
        val errorStockMinimo = when {
            stockMinimo == null -> "El stock mínimo debe ser un número entero"
            stockMinimo < 0 -> "El stock mínimo no puede ser negativo"
            else -> null
        }

        _estadoFormulario.update {
            it.copy(
                errores = ErroresProducto(
                    errorCodigo = errorCodigo,
                    errorNombre = errorNombre,
                    errorCategoria = errorCategoria,
                    errorPrecio = errorPrecio,
                    errorStockMinimo = errorStockMinimo
                )
            )
        }
        return errorCodigo == null && errorNombre == null && errorCategoria == null &&
                errorPrecio == null && errorStockMinimo == null
    }

    fun guardarProducto(): Boolean {
        if (!validarFormulario()) return false

        val estado = _estadoFormulario.value
        val producto = Producto(
            codigo = estado.codigo.trim(),
            nombre = estado.nombre.trim(),
            descripcion = estado.descripcion.trim(),
            categoria = estado.categoria,
            tipo = estado.tipo,
            precio = estado.precio.toInt(),
            stockActual = estado.stockActual.toIntOrNull() ?: 0,
            stockMinimo = estado.stockMinimo.toInt(),
            detalle = estado.detalle.ifBlank { "" }
        )

        _productos.update { it + producto }
        _estadoFormulario.value = FormularioProductoEstado()
        return true
    }
}