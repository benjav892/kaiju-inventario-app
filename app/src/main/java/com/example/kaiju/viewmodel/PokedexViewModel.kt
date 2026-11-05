package com.example.kaiju.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaiju.repository.PokemonRepository
import com.google.gson.JsonParseException

import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class PokedexViewModel(private val repository: PokemonRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(PokedexUiState())
    val uiState = _uiState.asStateFlow() // La pantalla solo puede observarlo.

    fun actualizarConsulta(valor: String) {
        if (_uiState.value.cargando) return
        _uiState.update { it.copy(consulta = valor, error = null, pokemon = null) }
    }

    fun buscar() {
        if (_uiState.value.cargando) return // Impide solicitudes simultáneas.
        val consulta = _uiState.value.consulta.trim()
        if (consulta.isBlank()) {
            _uiState.update { it.copy(error = "Escribe un nombre o número.", pokemon = null) }
            return
        }

        _uiState.update { it.copy(cargando = true, error = null, pokemon = null) }
        viewModelScope.launch {
            try {
                val pokemon = repository.buscar(consulta)
                _uiState.update { it.copy(cargando = false, pokemon = pokemon) }
            } catch (e: CancellationException) {
                // Cancelar la corrutina no es un error de conexión.
                throw e
            } catch (e: HttpException) {
                mostrarError(if (e.code() == 404) "No encontré ese Pokémon. Revisa el nombre o número."
                else "El servidor respondió con error ${e.code()}. Intenta nuevamente.")
            } catch (e: IOException) {
                mostrarError("No pude conectar. Revisa internet e intenta nuevamente.")
            } catch (e: JsonParseException) {
                mostrarError("La respuesta no tiene el formato esperado.")
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        _uiState.update { it.copy(cargando = false, pokemon = null, error = mensaje) }
    }
}