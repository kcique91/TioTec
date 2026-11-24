package duoc.appmoviles.network

import duoc.appmoviles.model.Dispositivo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Endpoints del microservicio ms-dispositivos (puerto 8082)
interface DispositivoApiService {

    // GET: lista todos los dispositivos
    @GET("api/dispositivos")
    suspend fun listar(): List<Dispositivo>

    // POST: registra un dispositivo; el servidor devuelve el dispositivo con su id
    @POST("api/dispositivos")
    suspend fun crear(@Body dispositivo: Dispositivo): Dispositivo

    // PUT: actualiza nombre y sistema de un dispositivo
    @PUT("api/dispositivos/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body dispositivo: Dispositivo): Dispositivo

    // DELETE: elimina un dispositivo; la respuesta no trae datos (204)
    @DELETE("api/dispositivos/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
}
