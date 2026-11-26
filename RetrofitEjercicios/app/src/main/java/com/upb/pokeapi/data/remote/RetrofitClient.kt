package com.upb.pokeapi.data.remote

/** Una sola instancia de Retrofit por baseUrl. */
object RetrofitClient {
    // Deben terminar en "/" para que Retrofit pueda concatenar las rutas relativas.
    private const val BASE_URL_POKEAPI = "https://pokeapi.co/api/v2/"
    private const val BASE_URL_JSONPLACEHOLDER = "https://jsonplaceholder.typicode.com/"
    private const val BASE_URL_CURSO = "https://clgqxx5idk.g4.sqlite.cloud:443/v2/functions/"

    val api: PokeApiService by lazy {
        FabricaRetrofit.crear(BASE_URL_POKEAPI).create(PokeApiService::class.java)
    }

    val publicacionesApi: PublicacionesService by lazy {
        FabricaRetrofit.crear(BASE_URL_JSONPLACEHOLDER).create(PublicacionesService::class.java)
    }

    val directorioApi: DirectorioService by lazy {
        FabricaRetrofit.crear(BASE_URL_CURSO).create(DirectorioService::class.java)
    }
}
