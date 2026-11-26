package com.upb.pokeapi.data.remote

import com.upb.pokeapi.BuildConfig
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FabricaRetrofit {

    private val clienteHttp: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS) // tiempo maximo para conectarse
            .readTimeout(15, TimeUnit.SECONDS)    // tiempo maximo esperando la respuesta
            .writeTimeout(15, TimeUnit.SECONDS)   // tiempo maximo enviando el cuerpo
        if (BuildConfig.DEBUG) {
            // Ejercicio 4: Level.BODY muestra en Logcat (filtro "OkHttp") lo que se envia y lo que llega.
            // Solo en desarrollo: en la app publicada no se debe registrar el contenido de las peticiones.
            builder.addInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            )
        }
        builder.build()
    }

    /** `baseUrl` debe terminar en "/". */
    fun crear(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}
