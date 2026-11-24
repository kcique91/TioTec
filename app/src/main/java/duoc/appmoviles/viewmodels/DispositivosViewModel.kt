package duoc.appmoviles.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duoc.appmoviles.model.Dispositivo
import duoc.appmoviles.model.DispositivoFormState
import duoc.appmoviles.repository.DispositivoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel de los dispositivos de prueba del perfil.
// Los datos viven en el microservicio ms-dispositivos; este ViewModel los pide al repositorio.
// Recibe el repositorio por el constructor para poder reemplazarlo en las pruebas.
class DispositivosViewModel(
    private val repository: DispositivoRepository = DispositivoRepository()
) : ViewModel() {

    // Lista de dispositivos: privada para modificar, pública solo de lectura
    private val _dispositivos = MutableStateFlow<List<Dispositivo>>(emptyList())
    val dispositivos: StateFlow<List<Dispositivo>> = _dispositivos.asStateFlow()

    // true mientras se espera la respuesta del microservicio
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // Mensaje de error (null si todo salió bien)
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Estado del formulario: nombre, sistema y sus errores
    private val _formDispositivo = MutableStateFlow(DispositivoFormState())
    val formDispositivo: StateFlow<DispositivoFormState> = _formDispositivo.asStateFlow()

    // Al abrir la pantalla se cargan los dispositivos
    init {
        cargarDispositivos()
    }

    // GET: pide todos los dispositivos al microservicio
    fun cargarDispositivos() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                _dispositivos.value = repository.listar()
            } catch (e: Exception) {
                Log.e("TioTec", "Error en Dispositivos", e)
                _error.value = "No se pudo conectar con el microservicio de dispositivos. Revisa que esté encendido."
            } finally {
                _cargando.value = false
            }
        }
    }

    // Actualiza el nombre del dispositivo y limpia su error
    fun onNombreDispositivoChange(valor: String) {
        _formDispositivo.update { it.copy(nombre = valor, errorNombre = null) }
    }

    // Actualiza el sistema operativo y limpia su error
    fun onSistemaDispositivoChange(valor: String) {
        _formDispositivo.update { it.copy(sistema = valor, errorSistema = null) }
    }

    // POST: valida el formulario y, si está bien, registra el dispositivo en el microservicio.
    // Devuelve true si el formulario era válido.
    fun agregarDispositivo(): Boolean {
        val form = _formDispositivo.value
        val errorNombre = if (form.nombre.isBlank()) "Campo obligatorio" else null
        val errorSistema = if (form.sistema.isBlank()) "Campo obligatorio" else null
        _formDispositivo.update { it.copy(errorNombre = errorNombre, errorSistema = errorSistema) }
        if (errorNombre != null || errorSistema != null) return false

        viewModelScope.launch {
            try {
                val creado = repository.crear(form.nombre.trim(), form.sistema.trim())
                // El nuevo queda arriba de la lista y el formulario se limpia
                _dispositivos.value = listOf(creado) + _dispositivos.value
                _formDispositivo.value = DispositivoFormState()
                _error.value = null
            } catch (e: Exception) {
                Log.e("TioTec", "Error en Dispositivos", e)
                _error.value = "No se pudo registrar el dispositivo"
            }
        }
        return true
    }

    // DELETE: elimina un dispositivo y lo quita de la lista
    fun eliminarDispositivo(dispositivo: Dispositivo) {
        val id = dispositivo.id ?: return
        viewModelScope.launch {
            try {
                if (repository.eliminar(id)) {
                    _dispositivos.value = _dispositivos.value.filter { it.id != id }
                } else {
                    _error.value = "No se pudo eliminar el dispositivo"
                }
            } catch (e: Exception) {
                Log.e("TioTec", "Error en Dispositivos", e)
                _error.value = "No se pudo eliminar el dispositivo"
            }
        }
    }
}
