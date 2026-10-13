package duoc.appmoviles.viewmodels

import androidx.lifecycle.ViewModel
import duoc.appmoviles.navigation.NavigationEvent
import duoc.appmoviles.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // Canal privado: solo el ViewModel puede emitir eventos
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()

    // Versión de solo lectura: la pantalla solo puede observar los eventos
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    // Emite el evento para ir a una pantalla
    fun navigateTo(screen: Screen) {
        CoroutineScope(Dispatchers.Main).launch {
            _navigationEvents.emit(NavigationEvent.NavigateTo(route = screen))
        }
    }

    // Emite el evento para volver atrás
    fun navigateBack() {
        CoroutineScope(Dispatchers.Main).launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }

    // Emite el evento para navegar hacia arriba
    fun navigateUp() {
        CoroutineScope(Dispatchers.Main).launch {
            _navigationEvents.emit(NavigationEvent.NavigateUp)
        }
    }
}

