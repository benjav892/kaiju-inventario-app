package com.example.kaiju.remote

import com.example.kaiju.model.PokemonDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApi {
    // La ruta se agrega a https://pokeapi.co/api/v2/.
    @GET("pokemon/{nombre}")
    suspend fun obtenerPokemon(@Path("nombre") nombre: String): PokemonDto
}