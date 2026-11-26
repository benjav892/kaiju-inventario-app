package com.example.kaiju.model

// si el campo es null, significa que no hay error (es valido)
data class ErroresLogin(
    val errorEmail: String? = null,
    val errorPassword: String? = null,
    val errorGeneral: String? = null
)
