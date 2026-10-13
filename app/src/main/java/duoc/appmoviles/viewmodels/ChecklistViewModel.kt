package duoc.appmoviles.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import duoc.appmoviles.data.AccionEntity
import duoc.appmoviles.data.AppDatabase
import duoc.appmoviles.model.Accion
import duoc.appmoviles.model.Categoria
import duoc.appmoviles.model.Pregunta
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChecklistViewModel(application: Application) : AndroidViewModel(application) {

    // DAO de las acciones guardadas en el teléfono
    private val dao = AppDatabase.obtener(application).accionDao()

    // Acciones guardadas en la base de datos. Cada fila (AccionEntity)
    // se convierte en una Accion, que es la que usa la pantalla.
    val acciones: StateFlow<List<Accion>> = dao.obtenerTodas()
        .map { filas ->
            filas.map {
                Accion(
                    id = it.id,
                    categoria = Categoria.valueOf(it.categoria),
                    texto = it.texto,
                    completada = it.completada
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Convierte las brechas de la evaluación en acciones y las guarda.
    // Si una acción ya existía, conserva su estado (pendiente o completada).
    fun cargarDesdeBrechas(brechas: List<Pregunta>) {
        viewModelScope.launch {
            val actuales = dao.obtenerTodas().first()
            val nuevas = brechas.map { pregunta ->
                AccionEntity(
                    id = pregunta.id,
                    categoria = pregunta.categoria.name,
                    texto = pregunta.recomendacion,
                    completada = actuales.find { it.id == pregunta.id }?.completada ?: false
                )
            }
            dao.insertarTodas(nuevas)
            dao.borrarExcepto(nuevas.map { it.id })
        }
    }

    // Cambia una acción de pendiente a completada, o al revés
    fun alternar(id: Int) {
        viewModelScope.launch {
            val accion = acciones.value.find { it.id == id } ?: return@launch
            dao.actualizarEstado(id, !accion.completada)
        }
    }
}