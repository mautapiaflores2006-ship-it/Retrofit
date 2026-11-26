package com.upb.pokeapi.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// Lo que se ENVIA (el cuerpo de la peticion):
data class NuevaPublicacionRequest(
    val title: String,
    val body: String,
    val userId: Int
)

// Lo que se RECIBE:
data class PublicacionDto(
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int
)

interface PublicacionesService {

    /** GET con parametro de consulta: /posts?userId=1 */
    @GET("posts")
    suspend fun listar(@Query("userId") usuarioId: Int): List<PublicacionDto>

    /** POST: el objeto se convierte a JSON y viaja en el cuerpo. */
    @POST("posts")
    suspend fun crear(@Body publicacion: NuevaPublicacionRequest): PublicacionDto

    /** PUT: reemplaza un recurso. El id va en la ruta y los datos en el cuerpo. */
    @PUT("posts/{id}")
    suspend fun reemplazar(@Path("id") id: Int, @Body publicacion: PublicacionDto): PublicacionDto

    /** DELETE: normalmente no devuelve cuerpo, por eso se usa Response<Unit> para leer solo el codigo. */
    @DELETE("posts/{id}")
    suspend fun eliminar(@Path("id") id: Int): Response<Unit>
}
