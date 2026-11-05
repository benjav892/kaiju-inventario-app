package com.example.kaiju.viewmodel

import com.example.kaiju.model.PokemonDto

data class PokedexUiState(
    val consulta: String = "pikachu",
    val cargando: Boolean = false,
    val pokemon: PokemonDto? = null,
    val error: String? = null
)