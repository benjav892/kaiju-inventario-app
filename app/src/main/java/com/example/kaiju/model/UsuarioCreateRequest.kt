package com.example.kaiju.model

data class UsuarioCreateRequest(
    val nombre: String,
    val apellido: String,
    val email: String,
    val password: String,
    val numeroTelefono: String?,
    val rol: String
)