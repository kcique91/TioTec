package duoc.appmoviles.network

import duoc.appmoviles.model.Solicitud
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Endpoints del microservicio ms-solicitudes (puerto 8081)
interface SolicitudApiService {

    // GET: lista todas las solicitudes
    @GET("api/solicitudes")
    suspend fun listar(): List<Solicitud>

    // POST: crea una solicitud; el servidor devuelve la solicitud con su id
    @POST("api/solicitudes")
    suspend fun crear(@Body solicitud: Solicitud): Solicitud

    // PUT: actualiza una solicitud existente (por ejemplo, su estado)
    @PUT("api/solicitudes/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body solicitud: Solicitud): Solicitud

    // DELETE: elimina una solicitud; la respuesta no trae datos (204)
    @DELETE("api/solicitudes/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
}
