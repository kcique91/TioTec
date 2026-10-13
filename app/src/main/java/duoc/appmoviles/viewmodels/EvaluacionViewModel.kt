package duoc.appmoviles.viewmodels

import androidx.lifecycle.ViewModel
import duoc.appmoviles.model.BancoPreguntas
import duoc.appmoviles.model.Pregunta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Estado de la pantalla de evaluación
data class EvaluacionUiState(
    val indice: Int = 0,                                // pregunta actual
    val respuestas: Map<Int, Boolean> = emptyMap(),     // id de pregunta -> true si cumple
    val terminada: Boolean = false                      // true cuando respondió todas
)

class EvaluacionViewModel : ViewModel() {

    val preguntas: List<Pregunta> = BancoPreguntas.preguntas

    // Estado privado: solo el ViewModel puede cambiarlo
    private val _estado = MutableStateFlow(EvaluacionUiState())

    // Versión de solo lectura para la pantalla
    val estado: StateFlow<EvaluacionUiState> = _estado.asStateFlow()

    // Guarda la respuesta de la pregunta actual y avanza a la siguiente
    fun responder(cumple: Boolean) {
        val actual = _estado.value
        val pregunta = preguntas[actual.indice]
        val esUltima = actual.indice == preguntas.lastIndex

        _estado.value = actual.copy(
            respuestas = actual.respuestas + (pregunta.id to cumple),
            indice = if (esUltima) actual.indice else actual.indice + 1,
            terminada = esUltima
        )
    }

    // Vuelve a empezar la evaluación
    fun reiniciar() {
        _estado.value = EvaluacionUiState()
    }

    // Preguntas que el usuario respondió "No": son las brechas de seguridad
    fun brechas(): List<Pregunta> {
        return preguntas.filter { _estado.value.respuestas[it.id] == false }
    }

    // Porcentaje de medidas que el usuario sí cumple (0 a 100)
    fun puntaje(): Int {
        val cumplidas = _estado.value.respuestas.values.count { it }
        return cumplidas * 100 / preguntas.size
    }
}