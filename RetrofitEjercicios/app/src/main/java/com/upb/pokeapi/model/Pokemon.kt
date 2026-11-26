package com.upb.pokeapi.model

data class PokemonResumen(
    val id: Int,
    val nombre: String,
    val imagenUrl: String
)

data class Estadistica(
    val nombre: String,
    val valor: Int
)

data class PokemonDetalle(
    val id: Int,
    val nombre: String,
    val alturaMetros: Double,
    val pesoKg: Double,
    val imagenUrl: String,
    val tipos: List<String>,
    val estadisticas: List<Estadistica>
)
