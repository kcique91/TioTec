package duoc.appmoviles.network

import duoc.appmoviles.model.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Define los endpoints de la API de publicaciones
interface PostApiService {

    // GET: obtiene la lista de posts
    @GET("posts")
    suspend fun getPosts(): List<Post>

    // POST: crea un post nuevo (los datos viajan en el cuerpo de la solicitud)
    @POST("posts")
    suspend fun crearPost(@Body post: Post): Post

    // PUT: reemplaza un post existente; el id va en la dirección
    @PUT("posts/{id}")
    suspend fun actualizarPost(@Path("id") id: Int, @Body post: Post): Post

    // DELETE: elimina un post; la respuesta no trae datos
    @DELETE("posts/{id}")
    suspend fun eliminarPost(@Path("id") id: Int): Response<Unit>
}