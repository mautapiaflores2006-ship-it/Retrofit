package com.upb.pokeapi.model

data class Contacto(
    val tipo: String,
    val valor: String,
    val principal: Boolean
)

data class UsuarioDirectorio(
    val id: Int,
    val nombre: String,
    val cargo: String?,
    val area: String?,
    val contactos: List<Contacto>
)
