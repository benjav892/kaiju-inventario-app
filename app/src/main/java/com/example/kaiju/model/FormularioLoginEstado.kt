package com.example.kaiju.model

data class FormularioLoginEstado(
    val email: String = "",
    val password: String = "",
    val cargando: Boolean = false,
    val errores: ErroresLogin = ErroresLogin()
)
