package com.example.kaiju.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaiju.model.ErroresLogin
import com.example.kaiju.model.FormularioLoginEstado
import com.example.kaiju.model.LoginRequest
import com.example.kaiju.model.Rol
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.model.Usuario
import com.example.kaiju.remote.KaijuApiClient
import com.example.kaiju.remote.KaijuApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SesionViewModel(
    private val api: KaijuApiService = KaijuApiClient.api
) : ViewModel() {

    private val _usuarioActivo = MutableStateFlow<Usuario?>(null)
    val usuarioActivo: StateFlow<Usuario?> = _usuarioActivo.asStateFlow()

    private val _estadoFormulario = MutableStateFlow(FormularioLoginEstado())
    val estadoFormulario: StateFlow<FormularioLoginEstado> = _estadoFormulario.asStateFlow()

    fun actualizarEmail(valor: String) {
        _estadoFormulario.update {
            it.copy(
                email = valor,
                errores = it.errores.copy(errorEmail = null, errorGeneral = null)
            )
        }
    }

    fun actualizarPassword(valor: String) {
        _estadoFormulario.update {
            it.copy(
                password = valor,
                errores = it.errores.copy(errorPassword = null, errorGeneral = null)
            )
        }
    }

    fun iniciarSesion(onExito: () -> Unit) {
        val estadoActual = _estadoFormulario.value
        val emailLimpio = estadoActual.email.trim()
        val passwordActual = estadoActual.password

        val errorEmail = when {
            emailLimpio.isBlank() -> "Ingresa tu correo"
            !emailLimpio.contains("@") -> "Formato de correo inválido"
            else -> null
        }
        val errorPassword = if (passwordActual.isBlank()) "Ingresa tu contraseña" else null

        if (errorEmail != null || errorPassword != null) {
            _estadoFormulario.update {
                it.copy(errores = ErroresLogin(errorEmail = errorEmail, errorPassword = errorPassword))
            }
            return
        }

        _estadoFormulario.update { it.copy(cargando = true, errores = ErroresLogin()) }

        viewModelScope.launch {
            try {
                val respuesta = api.login(LoginRequest(email = emailLimpio, password = passwordActual))
                KaijuApiClient.tokenJwt = respuesta.accessToken

                val u = respuesta.usuario
                _usuarioActivo.value = Usuario(
                    id = u.id.toString(),
                    nombre = "${u.nombre} ${u.apellido}".trim(),
                    rol = Rol.desdeBackend(u.rol)
                )
                _estadoFormulario.value = FormularioLoginEstado()
                onExito()
            } catch (e: HttpException) {
                val mensaje = if (e.code() == 401 || e.code() == 400) {
                    "Correo o contraseña incorrectos"
                } else {
                    "Error del servidor (${e.code()})"
                }
                _estadoFormulario.update {
                    it.copy(cargando = false, errores = ErroresLogin(errorGeneral = mensaje))
                }
            } catch (e: IOException) {
                _estadoFormulario.update {
                    it.copy(
                        cargando = false,
                        errores = ErroresLogin(errorGeneral = "No se pudo conectar con el servidor")
                    )
                }
            }
        }
    }

    fun cerrarSesion() {
        KaijuApiClient.tokenJwt = null
        _usuarioActivo.value = null
        _estadoFormulario.value = FormularioLoginEstado()
    }

    fun puedeGestionarUsuarios(): Boolean =
        _usuarioActivo.value?.rol == Rol.ADMINISTRADOR

    fun puedeCrearEditarProductos(): Boolean =
        _usuarioActivo.value?.rol == Rol.ADMINISTRADOR

    fun puedeVerAlertas(): Boolean =
        _usuarioActivo.value?.rol == Rol.ADMINISTRADOR ||
                _usuarioActivo.value?.rol == Rol.ENCARGADO_INVENTARIO

    fun puedeVerHistorialMovimientos(): Boolean =
        _usuarioActivo.value?.rol == Rol.ADMINISTRADOR

    fun obtenerTiposMovimientoPermitidos(): List<TipoMovimiento> =
        when (_usuarioActivo.value?.rol) {
            Rol.VENDEDOR -> listOf(TipoMovimiento.SALIDA_VENTA)
            Rol.ENCARGADO_INVENTARIO -> TipoMovimiento.entries
            Rol.ADMINISTRADOR -> listOf(TipoMovimiento.ENTRADA, TipoMovimiento.AJUSTE)
            null -> emptyList()
        }
}