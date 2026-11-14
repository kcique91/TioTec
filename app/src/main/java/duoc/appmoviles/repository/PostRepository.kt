package duoc.appmoviles.repository

import duoc.appmoviles.model.Post
import duoc.appmoviles.network.PostApiService
import duoc.appmoviles.network.RetrofitClient

// Se encarga de obtener los datos: el ViewModel le pide todo a él.
// "open" permite crear una versión falsa de esta clase en las pruebas (Guía 15)
open class PostRepository(
    private val api: PostApiService = RetrofitClient.postsApi
) {

    // GET: obtiene los posts desde la API
    open suspend fun getPosts(): List<Post> {
        return api.getPosts()
    }

    // POST: crea un post y devuelve el que respondió el servidor
    open suspend fun crearPost(post: Post): Post {
        return api.crearPost(post)
    }

    // PUT: actualiza un post y devuelve el que respondió el servidor
    open suspend fun actualizarPost(post: Post): Post {
        return api.actualizarPost(post.id, post)
    }

    // DELETE: elimina un post; devuelve true si el servidor respondió bien
    open suspend fun eliminarPost(id: Int): Boolean {
        return api.eliminarPost(id).isSuccessful
    }
}