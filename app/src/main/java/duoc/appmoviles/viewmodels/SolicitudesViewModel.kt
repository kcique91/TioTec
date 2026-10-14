package duoc.appmoviles.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import duoc.appmoviles.data.AppDatabase
import duoc.appmoviles.data.SolicitudEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SolicitudesViewModel(application: Application) : AndroidViewModel(application) {

    // DAO de las solicitudes
    private val solicitudDao = AppDatabase.obtener(application).solicitudDao()

    // Solicitudes guardadas, de la más nueva a la más antigua
    val solicitudes: StateFlow<List<SolicitudEntity>> = solicitudDao.obtenerTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Crea una solicitud nueva; siempre parte en estado "Pendiente"
    fun crearSolicitud(categoria: String, descripcion: String) {
        viewModelScope.launch {
            solicitudDao.insertar(
                SolicitudEntity(
                    categoria = categoria,
                    descripcion = descripcion.trim(),
                    estado = "Pendiente",
                    fecha = System.currentTimeMillis()
                )
            )
        }
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