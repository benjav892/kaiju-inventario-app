package com.example.kaiju.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kaiju.model.Usuario
import com.example.kaiju.repository.DatosDePrueba
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SesionViewModel : ViewModel() {

    // Lista de usuarios de prueba
    val usuariosPrueba: List<Usuario> = DatosDePrueba.usuarios

    // Estado del usuario activo en sesión (null = nadie ha ingresado)
    private val _usuarioActivo = MutableStateFlow<Usuario?>(null)
    val usuarioActivo: StateFlow<Usuario?> = _usuarioActivo.asStateFlow()

    // Estado de selección en el Login
    private val _usuarioSeleccionadoId = MutableStateFlow<String?>(null)
    val usuarioSeleccionadoId: StateFlow<String?> = _usuarioSeleccionadoId.asStateFlow()

    private val _errorLogin = MutableStateFlow<String?>(null)
    val errorLogin: StateFlow<String?> = _errorLogin.asStateFlow()

    fun seleccionarUsuarioLogin(id: String) {
        _usuarioSeleccionadoId.value = id
        _errorLogin.value = null
    }

    fun iniciarSesion(): Boolean {
        val idSeleccionado = _usuarioSeleccionadoId.value
        if (idSeleccionado.isNullOrBlank()) {
            _errorLogin.value = "Debe seleccionar un usuario para ingresar"
            return false
        }

        val usuarioEncontrado = usuariosPrueba.find { it.id == idSeleccionado }
        if (usuarioEncontrado != null) {
            _usuarioActivo.value = usuarioEncontrado
            _errorLogin.value = null
            return true
        } else {
            _errorLogin.value = "Usuario no válido"
            return false
        }
    }

    fun cerrarSesion() {
        _usuarioActivo.value = null
        _usuarioSeleccionadoId.value = null
        _errorLogin.value = null
    }
}