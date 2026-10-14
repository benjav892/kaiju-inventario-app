package com.example.kaiju.model

import java.time.LocalDateTime

enum class TipoMovimiento {
    ENTRADA,
    SALIDA_VENTA,
    SALIDA_CONSUMO,
    AJUSTE
}

data class Movimiento(
    val id: String,
    val codigoProducto: String,
    val tipo: TipoMovimiento,
    val cantidad: Int,
    val motivo: String,
    val fecha: LocalDateTime = LocalDateTime.now(),
    val usuario: String
)