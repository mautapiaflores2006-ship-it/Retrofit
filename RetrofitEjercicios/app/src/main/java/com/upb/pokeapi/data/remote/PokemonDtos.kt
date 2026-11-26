package com.upb.pokeapi.data.remote

import com.google.gson.annotations.SerializedName

// GET https://pokeapi.co/api/v2/pokemon?limit=20&offset=0
data class PokemonListResponse(
    val count: Int,
    val next: String?, // puede ser null: por eso lleva "?"
    val results: List<PokemonItemDto>
)

data class PokemonItemDto(
    val name: String,
    val url: String
)

// GET https://pokeapi.co/api/v2/pokemon/pikachu  (solo los campos que usa la app)
data class PokemonDetailDto(
    val id: Int,
    val name: String,
    val height: Int, // decimetros
    val weight: Int, // hectogramos
    val sprites: SpritesDto,
    val types: List<TypeSlotDto>,
    val stats: List<StatSlotDto>
)

data class SpritesDto(
    @SerializedName("front_default") val frontDefault: String?
)

data class NamedResourceDto(
    val name: String,
    val url: String
)

data class TypeSlotDto(
    val slot: Int,
    val type: NamedResourceDto
)

data class StatSlotDto(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: NamedResourceDto
)
