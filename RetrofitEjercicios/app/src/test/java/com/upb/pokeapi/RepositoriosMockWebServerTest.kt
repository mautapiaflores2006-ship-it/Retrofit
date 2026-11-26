package com.upb.pokeapi

import com.upb.pokeapi.data.DirectorioRepository
import com.upb.pokeapi.data.PokemonRepository
import com.upb.pokeapi.data.remote.BuscarRequest
import com.upb.pokeapi.data.remote.DirectorioService
import com.upb.pokeapi.data.remote.FabricaRetrofit
import com.upb.pokeapi.data.remote.PokeApiService
import com.upb.pokeapi.util.Resultado
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Ejercicio 5: pruebas con MockWebServer (sin internet).
 *  1) respuesta 404          -> Resultado.Error con el codigo
 *  2) servidor caido         -> Resultado.Error de conexion (IOException)
 *  3) lista vacia en "data"  -> Resultado.Exito con lista vacia (no es un error)
 * Extras: paginacion con offset (ejercicio 2) y JSON-en-texto invalido (ejercicio 6).
 */
class RepositoriosMockWebServerTest {

    private lateinit var servidor: MockWebServer

    @Before fun preparar() { servidor = MockWebServer().apply { start() } }
    @After fun cerrar() { runCatching { servidor.shutdown() } } // algunas pruebas ya lo apagaron

    private fun json(cuerpo: String, codigo: Int = 200) =
        MockResponse().setResponseCode(codigo)
            .addHeader("Content-Type", "application/json")
            .setBody(cuerpo)

    private fun <T> servicio(clase: Class<T>): T =
        FabricaRetrofit.crear(servidor.url("/").toString()).create(clase)

    private fun cuerpoBusqueda() = BuscarRequest("clave-falsa", "", "", "20", "0")

    // ---- 1) 404 ----
    @Test fun respuesta_404_se_traduce_a_error_con_el_codigo() {
        servidor.enqueue(json("""{"detail":"Not found"}""", codigo = 404))
        val repositorio = PokemonRepository(servicio(PokeApiService::class.java))

        val resultado = runBlocking { repositorio.obtenerPokemon("no-existe") }

        assertTrue(resultado is Resultado.Error)
        assertTrue((resultado as Resultado.Error).mensaje.contains("404"))
        assertEquals("/pokemon/no-existe", servidor.takeRequest().path)
    }

    // ---- 2) servidor caido ----
    @Test fun servidor_caido_se_traduce_a_error_de_conexion() {
        val repositorio = PokemonRepository(servicio(PokeApiService::class.java))
        servidor.shutdown() // nadie escucha en ese puerto: ConnectException (es una IOException)

        val resultado = runBlocking { repositorio.obtenerPokemones(20, 0) }

        assertTrue(resultado is Resultado.Error)
        assertTrue((resultado as Resultado.Error).mensaje.contains("No hay conexión"))
    }

    // ---- 3) lista vacia en data ----
    @Test fun data_vacia_no_es_error_es_exito_con_lista_vacia() {
        servidor.enqueue(json("""{"data":[]}"""))
        val repositorio = DirectorioRepository(servicio(DirectorioService::class.java))

        val resultado = runBlocking { repositorio.buscar(cuerpoBusqueda()) }

        assertTrue(resultado is Resultado.Exito)
        assertTrue((resultado as Resultado.Exito).datos.isEmpty())
        val peticion = servidor.takeRequest()
        assertEquals("POST", peticion.method)
        assertEquals("/directorio_buscar", peticion.path)
        assertTrue(peticion.body.readUtf8().contains("\"apikey\":\"clave-falsa\""))
    }

    // ---- extra: ejercicio 2 (offset de la pagina siguiente) ----
    @Test fun cargar_mas_pide_el_offset_de_la_pagina_siguiente() {
        servidor.enqueue(
            json("""{"count":1351,"next":null,"results":[{"name":"raichu","url":"https://pokeapi.co/api/v2/pokemon/26/"}]}""")
        )
        val repositorio = PokemonRepository(servicio(PokeApiService::class.java))

        val resultado = runBlocking { repositorio.obtenerPokemones(limite = 20, desplazamiento = 20) }

        assertEquals("/pokemon?limit=20&offset=20", servidor.takeRequest().path)
        assertTrue(resultado is Resultado.Exito)
        assertEquals(26, (resultado as Resultado.Exito).datos.first().id)
    }

    // ---- extra: ejercicio 6 (segunda lectura correcta) ----
    @Test fun contactos_en_texto_se_convierten_en_lista() {
        val contactos = """[{\"tipo\":\"CELULAR\",\"valor\":\"+59170000003\",\"principal\":1}]"""
        servidor.enqueue(
            json("""{"data":[{"id":4,"nombre_completo":"Lucía Quispe Mamani","cargo":null,"area_codigo":"TI","contactos":"$contactos"}]}""")
        )
        val repositorio = DirectorioRepository(servicio(DirectorioService::class.java))

        val resultado = runBlocking { repositorio.buscar(cuerpoBusqueda()) }

        val usuario = (resultado as Resultado.Exito).datos.single()
        assertEquals("Lucía Quispe Mamani", usuario.nombre)
        assertEquals("TI", usuario.area)
        assertEquals("+59170000003", usuario.contactos.single().valor)
        assertTrue(usuario.contactos.single().principal)
    }

    // ---- extra: ejercicio 6 (texto que no es JSON -> JsonSyntaxException -> error inesperado) ----
    @Test fun contactos_con_texto_invalido_dan_error_inesperado() {
        servidor.enqueue(
            json("""{"data":[{"id":1,"nombre_completo":"Ana","cargo":null,"area_codigo":null,"contactos":"esto no es json"}]}""")
        )
        val repositorio = DirectorioRepository(servicio(DirectorioService::class.java))

        val resultado = runBlocking { repositorio.buscar(cuerpoBusqueda()) }

        assertTrue(resultado is Resultado.Error)
        assertEquals("Ocurrió un error inesperado.", (resultado as Resultado.Error).mensaje)
    }
}
