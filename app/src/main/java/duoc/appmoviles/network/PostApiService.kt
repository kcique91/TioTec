package duoc.appmoviles.network

import duoc.appmoviles.model.Post
import retrofit2.http.GET

// Define los endpoints de la API de publicaciones
interface PostApiService {

    // Solicitud GET a https://jsonplaceholder.typicode.com/posts
    @GET("posts")
    suspend fun getPosts(): List<Post>
}