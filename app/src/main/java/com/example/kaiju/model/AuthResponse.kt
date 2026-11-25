package com.example.kaiju.model

data class AuthResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val usuario: UsuarioResponse
)