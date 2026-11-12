package duoc.appmoviles.repository

import duoc.appmoviles.model.Post
import duoc.appmoviles.network.PostApiService
import duoc.appmoviles.network.RetrofitClient

// Se encarga de obtener los datos: el ViewModel le pide los posts a él
// "open" permite crear una versión falsa de esta clase en las pruebas (Guía 15)
open class PostRepository(
    private val api: PostApiService = RetrofitClient.postsApi
) {

    // Obtiene los posts desde la API
    open suspend fun getPosts(): List<Post> {
        return api.getPosts()
    }
}