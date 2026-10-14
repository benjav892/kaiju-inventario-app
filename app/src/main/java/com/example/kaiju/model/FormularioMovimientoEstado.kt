package com.example.kaiju.model

data class FormularioMovimientoEstado(
    val codigoProducto: String = "",
    val tipo: TipoMovimiento? = null,
    val cantidad: String = "",
    val motivo: String = "",
    val errores: ErroresMovimiento = ErroresMovimiento()
)

data class ErroresMovimiento(
    val errorProducto: String? = null,
    val errorTipo: String? = null,
    val errorCantidad: String? = null,
    val errorMotivo: String? = null
)