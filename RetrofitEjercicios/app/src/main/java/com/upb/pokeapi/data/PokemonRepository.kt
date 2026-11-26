package com.upb.pokeapi.data

import com.upb.pokeapi.data.remote.PokeApiService
import com.upb.pokeapi.data.remote.RetrofitClient
import com.upb.pokeapi.model.PokemonDetalle
import com.upb.pokeapi.model.PokemonResumen
import com.upb.pokeapi.util.Resultado
import com.upb.pokeapi.util.llamadaSegura

class PokemonRepository(private val api: PokeApiService = RetrofitClient.api) {

    suspend fun obtenerPokemones(limite: Int, desplazamiento: Int): Resultado<List<PokemonResumen>> =
        llamadaSegura {
            api.obtenerPokemones(limite, desplazamiento).results.map { it.aResumen() }
        }

    suspend fun obtenerPokemon(nombre: String): Resultado<PokemonDetalle> =
        llamadaSegura { api.obtenerPokemon(nombre).aDetalle() }
}
