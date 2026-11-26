package com.example.kaiju.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaiju.model.UsuarioCreateRequest
import com.example.kaiju.model.UsuarioResponse
import com.example.kaiju.model.UsuarioUpdateRequest
import com.example.kaiju.remote.KaijuApiClient
import com.example.kaiju.remote.KaijuApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class UsuariosViewModel(
    private val api: KaijuApiService = KaijuApiClient.api
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsuariosUiState())
    val uiState: StateFlow<UsuariosUiState> = _uiState.asStateFlow()

    val rolesDisponibles = listOf("Administrador", "Encargado de inventario", "Vendedor")

    fun cargarUsuarios() {
        _uiState.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            try {
                val lista = api.listarUsuarios()
                _uiState.update { it.copy(cargando = false, usuarios = lista) }
            } catch (e: HttpException) {
                _uiState.update {
                    it.copy(cargando = false, error = "Error al cargar usuarios (${e.code()})")
                }
            } catch (e: IOException) {
                _uiState.update {
                    it.copy(cargando = false, error = "Sin conexión con el servidor")
                }
            }
        }
    }

    fun abrirFormularioCrear() {
        _uiState.update {
            it.copy(
                mostrarFormulario = true,
                usuarioEditando = null,
                nombre = "",
                apellido = "",
                email = "",
                password = "",
                numeroTelefono = "",
                rolSeleccionado = "Vendedor",
                errorFormulario = null,
                mensajeExito = null
            )
        }
    }

    fun abrirFormularioEditar(usuario: UsuarioResponse) {
        _uiState.update {
            it.copy(
                mostrarFormulario = true,
                usuarioEditando = usuario,
                nombre = usuario.nombre,
                apellido = usuario.apellido,
                email = usuario.email,
                password = "",
                numeroTelefono = usuario.numeroTelefono.orEmpty(),
                rolSeleccionado = if (usuario.rol in rolesDisponibles) usuario.rol else "Vendedor",
                errorFormulario = null,
                mensajeExito = null
            )
        }
    }

    fun cerrarFormulario() {
        _uiState.update { it.copy(mostrarFormulario = false, usuarioEditando = null, errorFormulario = null) }
    }

    fun onNombreChange(v: String) = _uiState.update { it.copy(nombre = v, errorFormulario = null) }
    fun onApellidoChange(v: String) = _uiState.update { it.copy(apellido = v, errorFormulario = null) }
    fun onEmailChange(v: String) = _uiState.update { it.copy(email = v, errorFormulario = null) }
    fun onPasswordChange(v: String) = _uiState.update { it.copy(password = v, errorFormulario = null) }
    fun onTelefonoChange(v: String) = _uiState.update { it.copy(numeroTelefono = v, errorFormulario = null) }
    fun onRolChange(v: String) = _uiState.update { it.copy(rolSeleccionado = v, errorFormulario = null) }

    fun guardarUsuario() {
        val st = _uiState.value
        val nombre = st.nombre.trim()
        val apellido = st.apellido.trim()
        val email = st.email.trim().lowercase()
        val telefono = st.numeroTelefono.trim().ifBlank { null }

        if (nombre.isBlank() || apellido.isBlank()) {
            _uiState.update { it.copy(errorFormulario = "Nombre y apellido son obligatorios") }
            return
        }

        if (st.usuarioEditando == null) {
            if (email.isBlank() || !email.contains("@")) {
                _uiState.update { it.copy(errorFormulario = "Ingresa un correo válido (@duoc.cl o @gmail.com)") }
                return
            }
            if (st.password.length < 4) {
                _uiState.update { it.copy(errorFormulario = "La contraseña debe tener al menos 4 caracteres") }
                return
            }
        }

        _uiState.update { it.copy(cargando = true, errorFormulario = null) }

        viewModelScope.launch {
            try {
                if (st.usuarioEditando == null) {
                    api.crearUsuario(
                        UsuarioCreateRequest(
                            nombre = nombre,
                            apellido = apellido,
                            email = email,
                            password = st.password,
                            numeroTelefono = telefono,
                            rol = st.rolSeleccionado
                        )
                    )
                    _uiState.update {
                        it.copy(
                            mostrarFormulario = false,
                            mensajeExito = "Usuario creado correctamente"
                        )
                    }
                } else {
                    api.actualizarUsuario(
                        id = st.usuarioEditando.id,
                        request = UsuarioUpdateRequest(
                            nombre = nombre,
                            apellido = apellido,
                            numeroTelefono = telefono,
                            rol = st.rolSeleccionado
                        )
                    )
                    _uiState.update {
                        it.copy(
                            mostrarFormulario = false,
                            usuarioEditando = null,
                            mensajeExito = "Usuario actualizado correctamente"
                        )
                    }
                }
                cargarUsuarios()
            } catch (e: HttpException) {
                val msg = when (e.code()) {
                    409 -> "Ese correo ya está registrado"
                    400 -> "Datos inválidos (revisa dominio del correo o rol en el servidor)"
                    403 -> "No tienes permisos de Administrador"
                    else -> "Error del servidor (${e.code()})"
                }
                _uiState.update { it.copy(cargando = false, errorFormulario = msg) }
            } catch (e: IOException) {
                _uiState.update { it.copy(cargando = false, errorFormulario = "Error de red con el servidor") }
            }
        }
    }

    fun eliminarUsuario(id: Long) {
        _uiState.update { it.copy(cargando = true, error = null, mensajeExito = null) }
        viewModelScope.launch {
            try {
                val resp = api.eliminarUsuario(id)
                if (resp.isSuccessful) {
                    _uiState.update { it.copy(mensajeExito = "Usuario eliminado") }
                    cargarUsuarios()
                } else {
                    val msg = if (resp.code() == 409) {
                        "No puedes eliminar tu propia cuenta activa"
                    } else {
                        "No se pudo eliminar (${resp.code()})"
                    }
                    _uiState.update { it.copy(cargando = false, error = msg) }
                }
            } catch (e: IOException) {
                _uiState.update { it.copy(cargando = false, error = "Sin conexión con el servidor") }
            }
        }
    }
}