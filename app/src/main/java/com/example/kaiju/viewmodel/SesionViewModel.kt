package com.example.kaiju.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kaiju.model.ErroresLogin
import com.example.kaiju.model.FormularioLoginEstado
import com.example.kaiju.model.Rol
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.model.Usuario
import com.example.kaiju.repository.DatosDePrueba
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SesionViewModel : ViewModel() {

    val usuariosDisponibles: List<Usuario> = DatosDePrueba.usuariosIniciales
    private val _estadoFormulario = MutableStateFlow(FormularioLoginEstado())
    val estadoFormulario: StateFlow<FormularioLoginEstado> = _estadoFormulario.asStateFlow()

    private val _usuarioActivo = MutableStateFlow<Usuario?>(null)
    val usuarioActivo: StateFlow<Usuario?> = _usuarioActivo.asStateFlow()

    fun seleccionarUsuario(usuario: Usuario) {
        _estadoFormulario.update { estadoActual ->
            estadoActual.copy(
                usuarioSeleccionado = usuario,
                errores = ErroresLogin(errorUsuario = null)
            )
        }
    }

    fun validarLogin(): Boolean {
        val estado = _estadoFormulario.value
        val errorUsuario = if (estado.usuarioSeleccionado == null) {
            "Debe seleccionar un usuario de prueba para ingresar"
        } else {
            null
        }

        _estadoFormulario.update {
            it.copy(errores = ErroresLogin(errorUsuario = errorUsuario))
        }

        return errorUsuario == null
    }

    fun iniciarSesion(): Boolean {
        if (!validarLogin()) return false

        _usuarioActivo.value = _estadoFormulario.value.usuarioSeleccionado
        return true
    }

    fun cerrarSesion() {
        _usuarioActivo.value = null
        _estadoFormulario.value = FormularioLoginEstado()
    }

    // ---- Reglas de permisos por perfil/rol -----

    // crear y editar productos solo está permitido para el ADMINISTRADOR
    fun puedeCrearEditarProductos(): Boolean {
        return _usuarioActivo.value?.rol == Rol.ADMINISTRADOR
    }

    // ver pantalla de alertas corresponde a ENCARGADO_INVENTARIO y ADMINISTRADOR
    fun puedeVerAlertas(): Boolean {
        val rol = _usuarioActivo.value?.rol
        return rol == Rol.ADMINISTRADOR || rol == Rol.ENCARGADO_INVENTARIO
    }

    // El historial de movimientos solo está permitido para el ADMINISTRADOR
    fun puedeVerHistorialMovimientos(): Boolean {
        return _usuarioActivo.value?.rol == Rol.ADMINISTRADOR
    }

    // devuelve que tipos de movimientos puede registrar el usuario activo
    fun obtenerTiposMovimientoPermitidos(): List<TipoMovimiento> {
        return when (_usuarioActivo.value?.rol) {
            Rol.ADMINISTRADOR -> listOf(
                TipoMovimiento.ENTRADA,
                TipoMovimiento.AJUSTE
            )
            Rol.ENCARGADO_INVENTARIO -> listOf(
                TipoMovimiento.ENTRADA,
                TipoMovimiento.SALIDA_VENTA,
                TipoMovimiento.SALIDA_CONSUMO,
                TipoMovimiento.AJUSTE
            )
            Rol.VENDEDOR -> listOf(
                TipoMovimiento.SALIDA_VENTA
            )
            null -> emptyList()
        }
    }
}