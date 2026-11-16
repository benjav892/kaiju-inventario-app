package com.example.kaiju.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaiju.model.ErroresMovimiento
import com.example.kaiju.model.ErroresProducto
import com.example.kaiju.model.FormularioMovimientoEstado
import com.example.kaiju.model.FormularioProductoEstado
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.repository.InventarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    // limpia el formulario cuando se va a crear un producto desde cero
    fun prepararNuevoProducto() {
        _estadoFormulario.value = FormularioProductoEstado()
    }

    // precarga todos los datos del producto seleccionado para editarlo
    fun cargarProductoParaEdicion(codigoProducto: String) {
        val producto = productos.value.find { it.codigo == codigoProducto } ?: return
        _estadoFormulario.value = FormularioProductoEstado(
            codigo = producto.codigo,
            nombre = producto.nombre,
            descripcion = producto.descripcion,
            categoria = producto.categoria,
            tipo = producto.tipo,
            precio = producto.precio.toString(),
            stockActual = producto.stockActual.toString(),
            stockMinimo = producto.stockMinimo.toString(),
            detalle = producto.detalle,
            imagenUri = producto.imagenUri,
            esEdicion = true,
            errores = ErroresProducto()
        )
    }

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
            !estado.esEdicion && productos.value.any { it.codigo == codigo } -> "Ese código ya existe"
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

    // Persistencia en base de datos local (Room) para creación y edición
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
            detalle = estado.detalle.ifBlank { "" },
            imagenUri = estado.imagenUri
        )

        viewModelScope.launch {
            repository.insertarProducto(producto)
        }

        _estadoFormulario.value = FormularioProductoEstado()
        return true
    }

    fun actualizarImagenProducto(codigoProducto: String, nuevaImagenUri: String) {
        val productoActual = productos.value.find { it.codigo == codigoProducto } ?: return
        val productoActualizado = productoActual.copy(imagenUri = nuevaImagenUri)
        viewModelScope.launch {
            repository.insertarProducto(productoActualizado)
        }
    }

    // SECCIÓN FORMULARIO DE MOVIMIENTOS
    private val _estadoFormularioMovimiento = MutableStateFlow(FormularioMovimientoEstado())
    val estadoFormularioMovimiento: StateFlow<FormularioMovimientoEstado> = _estadoFormularioMovimiento.asStateFlow()

    fun actualizarCodigoProductoMovimiento(valor: String) = _estadoFormularioMovimiento.update {
        it.copy(codigoProducto = valor, errores = it.errores.copy(errorProducto = null, errorGeneral = null))
    }
    fun actualizarTipoMovimiento(valor: TipoMovimiento) = _estadoFormularioMovimiento.update {
        it.copy(tipo = valor, errores = it.errores.copy(errorTipo = null, errorCantidad = null, errorGeneral = null))
    }
    fun actualizarCantidadMovimiento(valor: String) = _estadoFormularioMovimiento.update {
        it.copy(cantidad = valor, errores = it.errores.copy(errorCantidad = null, errorGeneral = null))
    }
    fun actualizarMotivoMovimiento(valor: String) = _estadoFormularioMovimiento.update {
        it.copy(motivo = valor, errores = it.errores.copy(errorMotivo = null, errorGeneral = null))
    }

    fun validarFormularioMovimiento(): Boolean {
        val estado = _estadoFormularioMovimiento.value
        val cantidadNumerica = estado.cantidad.toIntOrNull()

        // producto obligatorio
        val errorProducto = if (estado.codigoProducto.isBlank()) "Debe seleccionar un producto" else null

        // cantidad obligatoria y entero mayor a cero
        val errorCantidad = when {
            estado.cantidad.isBlank() -> "La cantidad es obligatoria"
            cantidadNumerica == null || cantidadNumerica <= 0 -> "La cantidad debe ser un entero mayor a cero"
            else -> null
        }

        val errorMotivo = if (estado.tipo == TipoMovimiento.AJUSTE && estado.motivo.isBlank()) {
            "El motivo es obligatorio para ajustes de stock"
        } else null

        _estadoFormularioMovimiento.update {
            it.copy(
                errores = ErroresMovimiento(
                    errorProducto = errorProducto,
                    errorTipo = null,
                    errorCantidad = errorCantidad,
                    errorMotivo = errorMotivo,
                    errorGeneral = null
                )
            )
        }

        return errorProducto == null && errorCantidad == null && errorMotivo == null
    }

    fun guardarMovimiento(
        usuario: String = "Sistema",
        onExito: () -> Unit = {}
    ) {
        if (!validarFormularioMovimiento()) return
        val fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

        val estado = _estadoFormularioMovimiento.value
        val movimiento = Movimiento(
            codigoProducto = estado.codigoProducto,
            tipo = estado.tipo,
            cantidad = estado.cantidad.toIntOrNull() ?: 0,
            motivo = estado.motivo.trim(),
            fecha = fechaActual,
            usuario = usuario
        )

        viewModelScope.launch {
            val resultado = repository.registrarMovimiento(movimiento)
            resultado
                .onSuccess {
                    _estadoFormularioMovimiento.value = FormularioMovimientoEstado()
                    onExito()
                }
                .onFailure { error ->
                    val mensajeRechazo = error.message ?: "El repositorio rechazó el movimiento."
                    _estadoFormularioMovimiento.update { actual ->
                        actual.copy(
                            errores = actual.errores.copy(
                                errorCantidad = mensajeRechazo,
                                errorGeneral = mensajeRechazo
                            )
                        )
                    }
                }
        }
    }
}