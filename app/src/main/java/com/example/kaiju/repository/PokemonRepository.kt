package com.example.kaiju.repository

import com.example.kaiju.model.PokemonDto
import com.example.kaiju.remote.PokeApi
import java.util.Locale

class PokemonRepository(private val api: PokeApi) {
    // Caché en memoria: evita repetir consultas durante la vida del repositorio.
    private val cache = mutableMapOf<String, PokemonDto>()

    suspend fun buscar(entrada: String): PokemonDto {
        val clave = entrada.trim().lowercase(Locale.ROOT)
        cache[clave]?.let { return it }

        val pokemon = api.obtenerPokemon(clave)
        // La misma ficha se puede buscar por nombre o por número.
        cache[clave] = pokemon
        cache[pokemon.name] = pokemon
        cache[pokemon.id.toString()] = pokemon
        return pokemon
    }
}