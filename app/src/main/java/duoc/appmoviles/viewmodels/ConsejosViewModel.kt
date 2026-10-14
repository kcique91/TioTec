package duoc.appmoviles.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duoc.appmoviles.model.Consejo
import duoc.appmoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Los tres estados posibles de la pantalla de consejos
sealed class ConsejosEstado {
    data object Cargando : ConsejosEstado()
    data class Exito(val consejos: List<Consejo>) : ConsejosEstado()
    data class Error(val mensaje: String) : ConsejosEstado()
}

class ConsejosViewModel : ViewModel() {

    // Estado privado: solo el ViewModel lo puede cambiar
    private val _estado = MutableStateFlow<ConsejosEstado>(ConsejosEstado.Cargando)

    // Versión de solo lectura: la pantalla solo observa
    val estado: StateFlow<ConsejosEstado> = _estado.asStateFlow()

    // Al crearse el ViewModel, pide los consejos de inmediato
    init {
        cargarConsejos()
    }

    // Pide los consejos a la API; también sirve para el botón "Reintentar"
    fun cargarConsejos() {
        viewModelScope.launch {
            _estado.value = ConsejosEstado.Cargando
            try {
                // Llamada a la API con Retrofit
                val lista = RetrofitClient.api.obtenerConsejos()
                _estado.value = ConsejosEstado.Exito(lista)
            } catch (e: Exception) {
                // Sin internet, dirección incorrecta, JSON mal formado, etc.
                _estado.value = ConsejosEstado.Error(
                    "No se pudieron cargar los consejos. Revisa tu conexión."
                )
            }
        }
    }
}