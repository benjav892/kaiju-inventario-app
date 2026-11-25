package com.example.kaiju.model

enum class Rol {
    ADMINISTRADOR,
    VENDEDOR,
    ENCARGADO_INVENTARIO;

    companion object {
        fun desdeBackend(rolTexto: String?): Rol {
            return when (rolTexto?.trim()?.lowercase()) {
                "administrador", "admin" -> ADMINISTRADOR
                "encargado de inventario", "encargado_inventario" -> ENCARGADO_INVENTARIO
                else -> VENDEDOR
            }
        }
    }
}