package com.upb.pokeapi

import com.upb.pokeapi.data.remote.FabricaRetrofit
import com.upb.pokeapi.data.remote.NuevaPublicacionRequest
import com.upb.pokeapi.data.remote.PublicacionesService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/** Prueba del manual: el POST envia el objeto como JSON en el cuerpo. */
class PublicacionesTest {

    private lateinit var servidor: MockWebServer

    @Before fun preparar() { servidor = MockWebServer().apply { start() } }
    @After fun cerrar() { servidor.shutdown() }

    @Test fun post_envia_el_objeto_como_json_en_el_cuerpo() {
        servidor.enqueue(
            MockResponse().addHeader("Content-Type", "application/json")
                .setBody("""{"title":"Hola","body":"Mi primer POST","userId":1,"id":101}""")
        )
        val api = FabricaRetrofit.crear(servidor.url("/").toString()).create(PublicacionesService::class.java)

        val creada = runBlocking { api.crear(NuevaPublicacionRequest("Hola", "Mi primer POST", 1)) }

        val peticion = servidor.takeRequest()
        assertEquals("POST", peticion.method)
        assertEquals("/posts", peticion.path)
        assertEquals("""{"title":"Hola","body":"Mi primer POST","userId":1}""", peticion.body.readUtf8())
        assertEquals(101, creada.id)
    }
}
