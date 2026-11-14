package duoc.appmoviles.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duoc.appmoviles.model.Post
import duoc.appmoviles.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel que mantiene el estado de los posts obtenidos desde la API.
// Recibe el repositorio por el constructor para poder reemplazarlo en las pruebas.
class PostViewModel(
    private val repository: PostRepository = PostRepository()
) : ViewModel() {

    // Lista de posts: privada para modificar, pública solo de lectura
    private val _postList = MutableStateFlow<List<Post>>(emptyList())
    val postList: StateFlow<List<Post>> = _postList.asStateFlow()

    // true mientras se espera la respuesta de la API
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // Mensaje de error de la carga inicial (null si todo salió bien)
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Mensaje corto sobre el resultado de crear, editar o eliminar
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    // Se ejecuta al crear el ViewModel
    init {
        fetchPosts()
    }

    // GET: obtiene los posts en segundo plano
    fun fetchPosts() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                _postList.value = repository.getPosts()
            } catch (e: Exception) {
                _error.value = "No se pudieron cargar las publicaciones. Revisa tu conexión."
            } finally {
                _cargando.value = false
            }
        }
    }

    // POST: crea una publicación y la agrega arriba de la lista
    fun crearPost(titulo: String, contenido: String) {
        if (titulo.isBlank() || contenido.isBlank()) return
        viewModelScope.launch {
            try {
                val nuevo = Post(userId = 1, id = 0, title = titulo.trim(), body = contenido.trim())
                val creado = repository.crearPost(nuevo)
                _postList.value = listOf(creado) + _postList.value
                _mensaje.value = "POST: el servidor creó la publicación con id ${creado.id}"
            } catch (e: Exception) {
                _mensaje.value = "No se pudo crear la publicación"
            }
        }
    }

    // PUT: actualiza una publicación y reemplaza la tarjeta en la lista
    fun actualizarPost(post: Post, nuevoTitulo: String, nuevoContenido: String) {
        if (nuevoTitulo.isBlank() || nuevoContenido.isBlank()) return
        viewModelScope.launch {
            try {
                val cambiado = post.copy(title = nuevoTitulo.trim(), body = nuevoContenido.trim())
                val actualizado = repository.actualizarPost(cambiado)
                val lista = _postList.value.toMutableList()
                val indice = lista.indexOf(post)
                if (indice >= 0) lista[indice] = actualizado
                _postList.value = lista
                _mensaje.value = "PUT: publicación ${actualizado.id} actualizada"
            } catch (e: Exception) {
                _mensaje.value = "No se pudo actualizar. El servidor de prueba no guarda las publicaciones nuevas."
            }
        }
    }

    // DELETE: elimina una publicación y la quita de la lista
    fun eliminarPost(post: Post) {
        viewModelScope.launch {
            try {
                if (repository.eliminarPost(post.id)) {
                    _postList.value = _postList.value - post
                    _mensaje.value = "DELETE: publicación ${post.id} eliminada"
                } else {
                    _mensaje.value = "No se pudo eliminar la publicación"
                }
            } catch (e: Exception) {
                _mensaje.value = "No se pudo eliminar la publicación"
            }
        }
    }

    // La pantalla avisa que ya mostró el mensaje, para no repetirlo
    fun mensajeMostrado() {
        _mensaje.value = null
    }
}