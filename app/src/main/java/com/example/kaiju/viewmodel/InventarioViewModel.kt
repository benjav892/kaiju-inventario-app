package com.example.kaiju.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kaiju.model.*
import com.example.kaiju.repository.DatosDePrueba
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class InventarioViewModel : ViewModel() {

    // ==========================================
    // 1.
    // ==========================================
    private val _estadoFormulario = MutableStateFlow(FormularioProductoEstado())
    val estadoFormulario: StateFlow<FormularioProductoEstado> = _estadoFormulario.asStateFlow()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()
    init {
        // Carga de datos de prueba desde repository
        _productos.value = DatosDePrueba.productos
    }

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
            detalle = estado.detalle.ifBlank { null }
        )

        _productos.update { it + producto }
        _estadoFormulario.value = FormularioProductoEstado()
        return true
    }

    // ==========================================
    // 2. MOVIMIENTOS Y REGLAS DE NEGOCIO
    // ==========================================

    // Historial de movimientos registrados (RN-05)
    private val _movimientos = MutableStateFlow<List<Movimiento>>(emptyList())
    val movimientos: StateFlow<List<Movimiento>> = _movimientos.asStateFlow()

    // Estado del formulario de movimiento
    private val _estadoFormularioMovimiento = MutableStateFlow(FormularioMovimientoEstado())
    val estadoFormularioMovimiento: StateFlow<FormularioMovimientoEstado> = _estadoFormularioMovimiento.asStateFlow()

    // Funciones para actualizar los campos del formulario de movimiento
    fun actualizarCodigoProductoMovimiento(valor: String) = _estadoFormularioMovimiento.update { it.copy(codigoProducto = valor) }
    fun actualizarTipoMovimiento(valor: TipoMovimiento?) = _estadoFormularioMovimiento.update { it.copy(tipo = valor) }
    fun actualizarCantidadMovimiento(valor: String) = _estadoFormularioMovimiento.update { it.copy(cantidad = valor) }
    fun actualizarMotivoMovimiento(valor: String) = _estadoFormularioMovimiento.update { it.copy(motivo = valor) }

    // Validación de reglas de negocio RN-01 y RN-04
    fun validarFormularioMovimiento(): Boolean {
        val estado = _estadoFormularioMovimiento.value
        val textoBusqueda = estado.codigoProducto.trim()

        // Busca el producto por código o por nombre (sin importar mayúsculas/minúsculas)
        val productoSeleccionado = _productos.value.find {
            it.codigo.equals(textoBusqueda, ignoreCase = true) ||
                    it.nombre.equals(textoBusqueda, ignoreCase = true)
        }
        val cantidadNum = estado.cantidad.toIntOrNull()

        val errorProducto = when {
            textoBusqueda.isBlank() -> "Debe ingresar el código o nombre del producto"
            productoSeleccionado == null -> "Producto no encontrado (verifique código o nombre)"
            else -> null
        }

        val errorTipo = if (estado.tipo == null) "Debe seleccionar el tipo de movimiento" else null

        // RN-01: Control de stock para salidas
        val errorCantidad = when {
            estado.cantidad.isBlank() -> "La cantidad es obligatoria"
            cantidadNum == null || cantidadNum <= 0 -> "La cantidad debe ser mayor a cero"
            (estado.tipo == TipoMovimiento.SALIDA_VENTA || estado.tipo == TipoMovimiento.SALIDA_CONSUMO) &&
                    productoSeleccionado != null && cantidadNum > productoSeleccionado.stockActual ->
                "Stock insuficiente (Stock disponible: ${productoSeleccionado.stockActual})"
            else -> null
        }

        // RN-04: Motivo obligatorio para ajustes
        val errorMotivo = if (estado.tipo == TipoMovimiento.AJUSTE && estado.motivo.isBlank()) {
            "El motivo es obligatorio para un ajuste de inventario"
        } else null

        _estadoFormularioMovimiento.update {
            it.copy(
                errores = ErroresMovimiento(
                    errorProducto = errorProducto,
                    errorTipo = errorTipo,
                    errorCantidad = errorCantidad,
                    errorMotivo = errorMotivo
                )
            )
        }

        return errorProducto == null && errorTipo == null && errorCantidad == null && errorMotivo == null
    }

    fun guardarMovimiento(usuarioActual: String = "Admin"): Boolean {
        if (!validarFormularioMovimiento()) return false

        val estado = _estadoFormularioMovimiento.value
        val textoBusqueda = estado.codigoProducto.trim()

        // Encuentra el producto coincidente
        val productoSeleccionado = _productos.value.first {
            it.codigo.equals(textoBusqueda, ignoreCase = true) ||
                    it.nombre.equals(textoBusqueda, ignoreCase = true)
        }
        val cantidadNum = estado.cantidad.toInt()

        // 1. Crea el objeto movimiento usando el CÓDIGO OFICIAL del producto
        val nuevoMovimiento = Movimiento(
            id = System.currentTimeMillis().toString(),
            codigoProducto = productoSeleccionado.codigo,
            tipo = estado.tipo!!,
            cantidad = cantidadNum,
            motivo = estado.motivo.trim(),
            usuario = usuarioActual
        )

        // 2. Actualiza el stock en la lista de productos
        _productos.update { lista ->
            lista.map { producto ->
                if (producto.codigo == productoSeleccionado.codigo) {
                    val nuevoStock = when (estado.tipo) {
                        TipoMovimiento.ENTRADA -> producto.stockActual + cantidadNum
                        TipoMovimiento.SALIDA_VENTA, TipoMovimiento.SALIDA_CONSUMO -> producto.stockActual - cantidadNum
                        TipoMovimiento.AJUSTE -> cantidadNum
                    }
                    producto.copy(stockActual = nuevoStock)
                } else {
                    producto
                }
            }
        }

        _movimientos.update { it + nuevoMovimiento }
        _estadoFormularioMovimiento.value = FormularioMovimientoEstado()
        return true
    }
}