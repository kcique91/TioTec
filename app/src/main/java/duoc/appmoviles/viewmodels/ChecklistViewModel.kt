package duoc.appmoviles.viewmodels

import androidx.lifecycle.ViewModel
import duoc.appmoviles.model.Accion
import duoc.appmoviles.model.Pregunta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChecklistViewModel : ViewModel() {

    // Lista privada de acciones: solo el ViewModel puede cambiarla
    private val _acciones = MutableStateFlow<List<Accion>>(emptyList())

    // Versión de solo lectura para la pantalla
    val acciones: StateFlow<List<Accion>> = _acciones.asStateFlow()

    // Convierte las brechas de la evaluación en acciones del checklist.
    // Si una acción ya existía, conserva su estado (pendiente o completada).
    fun cargarDesdeBrechas(brechas: List<Pregunta>) {
        val actuales = _acciones.value
        _acciones.value = brechas.map { pregunta ->
            val existente = actuales.find { it.id == pregunta.id }
            Accion(
                id = pregunta.id,
                categoria = pregunta.categoria,
                texto = pregunta.recomendacion,
                completada = existente?.completada ?: false
            )
        }
    }

    // Cambia una acción de pendiente a completada, o al revés
    fun alternar(id: Int) {
        _acciones.value = _acciones.value.map { accion ->
            if (accion.id == id) accion.copy(completada = !accion.completada) else accion
        }
    }
}