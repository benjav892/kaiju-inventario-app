package com.example.kaiju.model

import com.google.gson.annotations.SerializedName

// DTO: representa solo los campos JSON que necesita esta pantalla.
data class PokemonDto(
    val id: Int,
    val name: String,
    val height: Int, // PokéAPI entrega decímetros.
    val weight: Int, // PokéAPI entrega hectogramos.
    val sprites: SpritesDto,
    val types: List<PokemonTypeDto>
)

data class SpritesDto(
    @SerializedName("front_default") val frontDefault: String?
)

data class PokemonTypeDto(val slot: Int, val type: TypeDto)
data class TypeDto(val name: String)