package com.example.kaiju.model

data class FormularioLoginEstado(
    val usuarioSeleccionado: Usuario? = null,
    val errores: ErroresLogin = ErroresLogin()
)
