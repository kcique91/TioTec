package duoc.appmoviles.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import duoc.appmoviles.data.AppDatabase
import duoc.appmoviles.data.SolicitudEntity
import duoc.appmoviles.model.SolicitudFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SolicitudesViewModel(application: Application) : AndroidViewModel(application) {

    // DAO de las solicitudes
    private val solicitudDao = AppDatabase.obtener(application).solicitudDao()

    // Solicitudes guardadas, de la más nueva a la más antigua
    val solicitudes: StateFlow<List<SolicitudEntity>> = solicitudDao.obtenerTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado del formulario: privado para modificar, público solo lectura
    private val _form = MutableStateFlow(SolicitudFormState())
    val form: StateFlow<SolicitudFormState> = _form.asStateFlow()

    // Actualiza la categoría elegida
    fun onCategoriaChange(valor: String) {
        _form.update { it.copy(categoria = valor) }
    }

    // Actualiza la descripción y limpia su error
    fun onDescripcionChange(valor: String) {
        _form.update { it.copy(descripcion = valor, errorDescripcion = null) }
    }

    // Valida el formulario; si está correcto crea la solicitud en estado "Pendiente".
    // Devuelve true si se creó.
    fun crearSolicitud(): Boolean {
        val actual = _form.value
        val error = when {
            actual.descripcion.isBlank() -> "Campo obligatorio"
            actual.descripcion.trim().length < 10 -> "Describe el problema con al menos 10 caracteres"
            else -> null
        }
        _form.update { it.copy(errorDescripcion = error) }
        if (error != null) return false

        viewModelScope.launch {
            solicitudDao.insertar(
                SolicitudEntity(
                    categoria = actual.categoria,
                    descripcion = actual.descripcion.trim(),
                    estado = "Pendiente",
                    fecha = System.currentTimeMillis()
                )
            )
        }
        // Limpia la descripción y mantiene la categoría elegida
        _form.update { it.copy(descripcion = "") }
        return true
    }

    // Simula el avance de la solicitud: Pendiente -> En revisión -> Resuelta
    fun avanzarEstado(solicitud: SolicitudEntity) {
        val nuevoEstado = when (solicitud.estado) {
            "Pendiente" -> "En revisión"
            else -> "Resuelta"
        }
        viewModelScope.launch {
            solicitudDao.actualizarEstado(solicitud.id, nuevoEstado)
        }
    }

    // Elimina una solicitud
    fun eliminarSolicitud(id: Int) {
        viewModelScope.launch {
            solicitudDao.eliminar(id)
        }
    }
}