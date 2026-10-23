package com.example.kaiju.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaiju.model.ErroresProducto
import com.example.kaiju.model.FormularioProductoEstado
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.repository.InventarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InventarioViewModel(
    private val repository: InventarioRepository
) : ViewModel() {

    init {
        // Precarga de datos iniciales si la BD de Room está vacía
        viewModelScope.launch {
            repository.precargarDatosSiEstaVacia()
        }
    }

    // Estado del formulario de creación/edición de producto
    private val _estadoFormulario = MutableStateFlow(FormularioProductoEstado())
    val estadoFormulario: StateFlow<FormularioProductoEstado> = _estadoFormulario.asStateFlow()

    // Flujos reactivos alimentados desde Room
    val productos: StateFlow<List<Producto>> = repository.productos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val movimientos: StateFlow<List<Movimiento>> = repository.movimientos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Funciones de actualización de campos del formulario
    fun actualizarCodigo(valor: String) = _estadoFormulario.update { it.copy(codigo = valor) }
    fun actualizarNombre(valor: String) = _estadoFormulario.update { it.copy(nombre = valor) }
    fun actualizarDescripcion(valor: String) = _estadoFormulario.update { it.copy(descripcion = valor) }
    fun actualizarCategoria(valor: String) = _estadoFormulario.update { it.copy(categoria = valor) }
    fun actualizarTipo(valor: String) = _estadoFormulario.update { it.copy(tipo = valor) }
    fun actualizarPrecio(valor: String) = _estadoFormulario.update { it.copy(precio = valor) }
    fun actualizarStockActual(valor: String) = _estadoFormulario.update { it.copy(stockActual = valor) }
    fun actualizarStockMinimo(valor: String) = _estadoFormulario.update { it.copy(stockMinimo = valor) }
    fun actualizarDetalle(valor: String) = _estadoFormulario.update { it.copy(detalle = valor) }

    // Validación de negocio para el formulario
    fun validarFormulario(): Boolean {
        val estado = _estadoFormulario.value
        val codigo = estado.codigo.trim()
        val precio = estado.precio.toIntOrNull()
        val stockMinimo = estado.stockMinimo.toIntOrNull()

        val errorCodigo = when {
            codigo.isBlank() -> "El código es obligatorio"
            productos.value.any { it.codigo == codigo } -> "Ese código ya existe"
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

    // Persistencia en base de datos local (Room)
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

        viewModelScope.launch {
            repository.insertarProducto(producto)
        }

        _estadoFormulario.value = FormularioProductoEstado()
        return true
    }

    fun registrarMovimiento(movimiento: Movimiento, onResultado: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val resultado = repository.registrarMovimiento(movimiento)
            onResultado(resultado)
        }
    }
}