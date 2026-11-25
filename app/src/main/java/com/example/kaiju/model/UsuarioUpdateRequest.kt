package com.example.kaiju.model

data class UsuarioUpdateRequest(
    val nombre: String,
    val apellido: String,
    val numeroTelefono: String?,
    val rol: String?
)