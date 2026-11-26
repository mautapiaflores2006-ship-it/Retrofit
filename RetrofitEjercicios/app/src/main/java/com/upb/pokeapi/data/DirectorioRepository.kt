package com.upb.pokeapi.data

import com.upb.pokeapi.data.remote.BuscarRequest
import com.upb.pokeapi.data.remote.DirectorioService
import com.upb.pokeapi.data.remote.RetrofitClient
import com.upb.pokeapi.model.UsuarioDirectorio
import com.upb.pokeapi.util.Resultado
import com.upb.pokeapi.util.llamadaSegura

class DirectorioRepository(
    private val api: DirectorioService = RetrofitClient.directorioApi
) {
    /** data vacia ([]) NO es un error: devuelve Exito con lista vacia ("sin resultados"). */
    suspend fun buscar(cuerpo: BuscarRequest): Resultado<List<UsuarioDirectorio>> =
        llamadaSegura { api.buscar(cuerpo).data.map { it.aUsuario() } }
}
