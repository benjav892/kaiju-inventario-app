package com.example.kaiju.viewmodel

import com.example.kaiju.model.UsuarioResponse

data class UsuariosUiState(
    val usuarios: List<UsuarioResponse> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null,
    val mensajeExito: String? = null,
    val mostrarFormulario: Boolean = false,
    val usuarioEditando: UsuarioResponse? = null,
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val password: String = "",
    val numeroTelefono: String = "",
    val rolSeleccionado: String = "Vendedor",
    val errorFormulario: String? = null
)