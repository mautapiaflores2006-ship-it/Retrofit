package com.upb.pokeapi.data

import com.upb.pokeapi.data.remote.NuevaPublicacionRequest
import com.upb.pokeapi.data.remote.PublicacionesService
import com.upb.pokeapi.data.remote.RetrofitClient
import com.upb.pokeapi.model.Publicacion
import com.upb.pokeapi.util.Resultado
import com.upb.pokeapi.util.llamadaSegura

class PublicacionesRepository(
    private val api: PublicacionesService = RetrofitClient.publicacionesApi
) {
    suspend fun crear(titulo: String, cuerpo: String, usuarioId: Int): Resultado<Publicacion> =
        llamadaSegura {
            api.crear(NuevaPublicacionRequest(title = titulo, body = cuerpo, userId = usuarioId)).aPublicacion()
        }
}
