package com.example.kaiju.model

data class Producto(
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val tipo: String,
    val precio: Int, // En CLP sin decimales (Guía 10)
    val stockActual: Int,
    val stockMinimo: Int,
    val detalle: String? = null
)