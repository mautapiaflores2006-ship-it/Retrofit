package com.upb.pokeapi.data

import com.google.gson.Gson
import com.upb.pokeapi.data.remote.ContactoDto
import com.upb.pokeapi.data.remote.PokemonDetailDto
import com.upb.pokeapi.data.remote.PokemonItemDto
import com.upb.pokeapi.data.remote.PublicacionDto
import com.upb.pokeapi.data.remote.UsuarioDto
import com.upb.pokeapi.data.remote.listaDesdeTexto
import com.upb.pokeapi.model.Contacto
import com.upb.pokeapi.model.Estadistica
import com.upb.pokeapi.model.PokemonDetalle
import com.upb.pokeapi.model.PokemonResumen
import com.upb.pokeapi.model.Publicacion
import com.upb.pokeapi.model.UsuarioDirectorio

private val gson = Gson()

private fun urlImagen(id: Int) =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

fun PokemonItemDto.aResumen(): PokemonResumen {
    val id = url.trimEnd('/').substringAfterLast('/').toInt() // ".../pokemon/25/" -> 25
    return PokemonResumen(id = id, nombre = name, imagenUrl = urlImagen(id))
}

fun PokemonDetailDto.aDetalle(): PokemonDetalle =
    PokemonDetalle(
        id = id,
        nombre = name,
        alturaMetros = height / 10.0,
        pesoKg = weight / 10.0,
        imagenUrl = sprites.frontDefault ?: urlImagen(id),
        tipos = types.map { it.type.name },
        estadisticas = stats.map { Estadistica(nombre = it.stat.name, valor = it.baseStat) }
    )

fun PublicacionDto.aPublicacion(): Publicacion =
    Publicacion(id = id, titulo = title, cuerpo = body, usuarioId = userId)

/** Aqui ocurre la "segunda lectura" del campo contactos (ejercicio 6). */
fun UsuarioDto.aUsuario(): UsuarioDirectorio =
    UsuarioDirectorio(
        id = id,
        nombre = nombreCompleto,
        cargo = cargo,
        area = areaCodigo,
        contactos = gson.listaDesdeTexto<ContactoDto>(contactos)
            .map { Contacto(tipo = it.tipo, valor = it.valor, principal = it.principal == 1) }
    )
