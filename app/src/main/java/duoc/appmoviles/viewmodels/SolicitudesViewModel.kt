package duoc.appmoviles.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duoc.appmoviles.model.Solicitud
import duoc.appmoviles.model.SolicitudFormState
import duoc.appmoviles.repository.SolicitudRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel de las solicitudes de asistencia.
// Los datos viven en el microservicio ms-solicitudes; este ViewModel los pide al repositorio.
// Recibe el repositorio por el constructor para poder reemplazarlo en las pruebas.
class SolicitudesViewModel(
    private val repository: SolicitudRepository = SolicitudRepository()
) : ViewModel() {

    // Lista de solicitudes: privada para modificar, pública solo de lectura
    private val _solicitudes = MutableStateFlow<List<Solicitud>>(emptyList())
    val solicitudes: StateFlow<List<Solicitud>> = _solicitudes.asStateFlow()

    // true mientras se espera la respuesta del microservicio
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // Mensaje de error (null si todo salió bien)
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Estado del formulario: categoría, descripción y su error
    private val _form = MutableStateFlow(SolicitudFormState())
    val form: StateFlow<SolicitudFormState> = _form.asStateFlow()

    // Al abrir la pantalla se cargan las solicitudes
    init {
        cargarSolicitudes()
    }

    // GET: pide todas las solicitudes al microservicio
    fun cargarSolicitudes() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                _solicitudes.value = repository.listar()
            } catch (e: Exception) {
                Log.e("TioTec", "Error en Solicitudes", e)
                _error.value = "No se pudo conectar con el microservicio de solicitudes. Revisa que esté encendido."
            } finally {
                _cargando.value = false
            }
        }
    }

    // Actualiza la categoría elegida
    fun onCategoriaChange(valor: String) {
        _form.update { it.copy(categoria = valor) }
    }

    // Actualiza la descripción y limpia su error
    fun onDescripcionChange(valor: String) {
        _form.update { it.copy(descripcion = valor, errorDescripcion = null) }
    }

    // Regla de validación de la descripción (null = sin error)
    fun validarDescripcion(texto: String): String? = when {
        texto.isBlank() -> "Campo obligatorio"
        texto.trim().length < 10 -> "Describe el problema con al menos 10 caracteres"
        else -> null
    }

    // POST: valida el formulario y, si está bien, crea la solicitud en el microservicio.
    // Devuelve true si el formulario era válido.
    fun crearSolicitud(): Boolean {
        val actual = _form.value
        val error = validarDescripcion(actual.descripcion)
        _form.update { it.copy(errorDescripcion = error) }
        if (error != null) return false

        viewModelScope.launch {
            try {
                val creada = repository.crear(actual.categoria, actual.descripcion.trim())
                // La nueva queda arriba de la lista
                _solicitudes.value = listOf(creada) + _solicitudes.value
                // Limpia la descripción y mantiene la categoría elegida
                _form.update { it.copy(descripcion = "") }
                _error.value = null
            } catch (e: Exception) {
                Log.e("TioTec", "Error en Solicitudes", e)
                _error.value = "No se pudo crear la solicitud"
            }
        }
        return true
    }

    // Estado que sigue: Pendiente -> En revisión -> Resuelta
    fun siguienteEstado(estado: String): String = when (estado) {
        "Pendiente" -> "En revisión"
        else -> "Resuelta"
    }

    // PUT: avanza el estado de una solicitud y reemplaza la tarjeta en la lista
    fun avanzarEstado(solicitud: Solicitud) {
        val cambiada = solicitud.copy(estado = siguienteEstado(solicitud.estado))
        viewModelScope.launch {
            try {
                val actualizada = repository.actualizar(cambiada)
                _solicitudes.value = _solicitudes.value.map {
                    if (it.id == actualizada.id) actualizada else it
                }
            } catch (e: Exception) {
                Log.e("TioTec", "Error en Solicitudes", e)
                _error.value = "No se pudo actualizar la solicitud"
            }
        }
    }

    // DELETE: elimina una solicitud y la quita de la lista
    fun eliminarSolicitud(solicitud: Solicitud) {
        val id = solicitud.id ?: return
        viewModelScope.launch {
            try {
                if (repository.eliminar(id)) {
                    _solicitudes.value = _solicitudes.value.filter { it.id != id }
                } else {
                    _error.value = "No se pudo eliminar la solicitud"
                }
            } catch (e: Exception) {
                Log.e("TioTec", "Error en Solicitudes", e)
                _error.value = "No se pudo eliminar la solicitud"
            }
        }
    }
}
