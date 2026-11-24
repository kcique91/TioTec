package duoc.appmoviles.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duoc.appmoviles.model.VersionAndroid
import duoc.appmoviles.repository.VersionAndroidRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ViewModel de la pantalla "Soporte de Android" (API externa endoflife.date).
// Recibe el repositorio y la fecha de hoy por el constructor para poder controlarlos en las pruebas.
class VersionesViewModel(
    private val repository: VersionAndroidRepository = VersionAndroidRepository(),
    private val hoy: () -> String = {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
) : ViewModel() {

    // Versiones obtenidas de la API
    private val _versiones = MutableStateFlow<List<VersionAndroid>>(emptyList())
    val versiones: StateFlow<List<VersionAndroid>> = _versiones.asStateFlow()

    // true mientras se espera la respuesta de la API
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // Mensaje de error (null si todo salió bien)
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargarVersiones()
    }

    // GET: pide la lista de versiones a la API externa
    fun cargarVersiones() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                _versiones.value = repository.obtenerVersiones()
            } catch (e: Exception) {
                Log.e("TioTec", "Error en Versiones", e)
                _error.value = "No se pudo consultar el soporte de Android. Revisa tu conexión a internet."
            } finally {
                _cargando.value = false
            }
        }
    }

    // ¿La versión sigue recibiendo parches de seguridad hoy?
    fun tieneSoporte(version: VersionAndroid): Boolean = version.tieneSoporte(hoy())

    // Texto del estado de soporte de la versión
    fun descripcionSoporte(version: VersionAndroid): String = version.descripcionSoporte(hoy())

    // Cuántas versiones siguen con soporte
    fun cantidadConSoporte(): Int = _versiones.value.count { tieneSoporte(it) }
}
