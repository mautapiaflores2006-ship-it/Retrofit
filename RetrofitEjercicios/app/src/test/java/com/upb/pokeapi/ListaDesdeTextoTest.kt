package com.upb.pokeapi

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.upb.pokeapi.data.remote.ContactoDto
import com.upb.pokeapi.data.remote.listaDesdeTexto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Ejercicio 6: la "segunda lectura" de un campo que llega como texto con JSON adentro. */
class ListaDesdeTextoTest {

    private val gson = Gson()

    @Test fun texto_valido_se_convierte_en_lista() {
        val texto = """[{"tipo":"CELULAR","valor":"+59170000003","principal":1}]"""
        val lista = gson.listaDesdeTexto<ContactoDto>(texto)
        assertEquals(listOf(ContactoDto("CELULAR", "+59170000003", 1)), lista)
    }

    @Test fun texto_nulo_o_en_blanco_da_lista_vacia() {
        assertTrue(gson.listaDesdeTexto<ContactoDto>(null).isEmpty())
        assertTrue(gson.listaDesdeTexto<ContactoDto>("   ").isEmpty())
    }

    @Test(expected = JsonSyntaxException::class)
    fun texto_que_no_es_json_lanza_JsonSyntaxException() {
        gson.listaDesdeTexto<ContactoDto>("esto no es json")
    }
}
