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

    // Mensaje de error (null si todo salió bien)
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Se ejecuta al crear el ViewModel
    init {
        fetchPosts()
    }

    // Obtiene los posts en segundo plano
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
}