package duoc.appmoviles.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import duoc.appmoviles.data.AppDatabase
import duoc.appmoviles.data.DispositivoEntity
import duoc.appmoviles.data.PerfilEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    // DAOs del perfil y de los dispositivos
    private val perfilDao = AppDatabase.obtener(application).perfilDao()
    private val dispositivoDao = AppDatabase.obtener(application).dispositivoDao()

    // Perfil guardado (null si todavía no se ha creado)
    val perfil: StateFlow<PerfilEntity?> = perfilDao.obtener()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Dispositivos de prueba guardados
    val dispositivos: StateFlow<List<DispositivoEntity>> = dispositivoDao.obtenerTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Crea o actualiza el perfil ficticio
    fun guardarPerfil(nombre: String, nivel: String) {
        viewModelScope.launch {
            perfilDao.guardar(PerfilEntity(nombre = nombre.trim(), nivel = nivel))
        }
    }

    // Registra un dispositivo de prueba
    fun agregarDispositivo(nombre: String, sistema: String) {
        viewModelScope.launch {
            dispositivoDao.insertar(
                DispositivoEntity(nombre = nombre.trim(), sistema = sistema.trim())
            )
        }
    }

    // Elimina un dispositivo
    fun eliminarDispositivo(id: Int) {
        viewModelScope.launch {
            dispositivoDao.eliminar(id)
        }
    }
}