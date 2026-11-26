package com.upb.pokeapi.util

import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.HttpException

/**
 * Ejecuta una llamada a la API y traduce las excepciones a [Resultado]
 * (misma tabla de excepciones del paso 8 del manual). Asi no se repite el try/catch
 * en cada funcion de cada repositorio.
 */
suspend fun <T> llamadaSegura(bloque: suspend () -> T): Resultado<T> =
    try {
        Resultado.Exito(bloque())
    } catch (e: CancellationException) {
        throw e // cancelar la corrutina no es un error
    } catch (e: HttpException) {
        Resultado.Error("El servidor respondió con un error (código ${e.code()}).")
    } catch (e: IOException) {
        Resultado.Error("No hay conexión a internet. Revisa tu red e inténtalo de nuevo.")
    } catch (e: Exception) {
        Resultado.Error("Ocurrió un error inesperado.")
    }
