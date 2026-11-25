package com.example.kaiju.model

data class UsuarioResponse(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val email: String,
    val numeroTelefono: String?,
    val rol: String,
    val fechaRegistro: String? = null
)