package duoc.appmoviles.viewmodels

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import duoc.appmoviles.data.AppDatabase
import duoc.appmoviles.data.DispositivoEntity
import duoc.appmoviles.data.PerfilEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

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

    // ----- Foto del perfil (cámara y galería) -----

    // Carpeta privada de la app donde se guarda la foto (la misma de file_paths.xml)
    private val carpetaFotos = File(application.filesDir, "imagenes").apply { mkdirs() }

    // Uri de la foto actual: privado para modificar, público solo lectura.
    // Al abrir la app se busca si ya había una foto guardada.
    private val _fotoUri = MutableStateFlow(buscarFotoGuardada())
    val fotoUri: StateFlow<Uri?> = _fotoUri.asStateFlow()

    // Archivo donde la cámara está guardando la foto que se está tomando
    private var archivoPendiente: File? = null

    // Crea un archivo vacío y devuelve su Uri segura (FileProvider)
    // para que la app de cámara escriba la foto ahí
    fun prepararFotoCamara(): Uri {
        val archivo = File(carpetaFotos, "foto_${System.currentTimeMillis()}.jpg")
        archivoPendiente = archivo
        val context = getApplication<Application>()
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
    }

    // Respuesta de la cámara: si tomó la foto se usa, si canceló se borra el archivo vacío
    fun resultadoCamara(exito: Boolean) {
        val archivo = archivoPendiente ?: return
        if (exito && archivo.length() > 0) {
            usarComoFotoActual(archivo)
        } else {
            archivo.delete()
        }
        archivoPendiente = null
    }

    // Respuesta de la galería: copia la imagen elegida a la carpeta de la app.
    // uri es null cuando el usuario cancela, en ese caso no se hace nada.
    fun resultadoGaleria(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val archivo = File(carpetaFotos, "foto_${System.currentTimeMillis()}.jpg")
                getApplication<Application>().contentResolver.openInputStream(uri)?.use { entrada ->
                    archivo.outputStream().use { salida -> entrada.copyTo(salida) }
                }
                usarComoFotoActual(archivo)
            } catch (e: Exception) {
                // Si la imagen no se puede leer, se mantiene la foto anterior
            }
        }
    }

    // Elimina la foto actual
    fun quitarFoto() {
        carpetaFotos.listFiles()?.forEach { it.delete() }
        _fotoUri.value = null
    }

    // Deja solo la foto nueva (borra las anteriores) y actualiza el estado
    private fun usarComoFotoActual(nueva: File) {
        carpetaFotos.listFiles()?.filter { it != nueva }?.forEach { it.delete() }
        _fotoUri.value = Uri.fromFile(nueva)
    }

    // Busca la última foto guardada en la carpeta (null si no hay)
    private fun buscarFotoGuardada(): Uri? {
        val ultima = carpetaFotos.listFiles()
            ?.filter { it.length() > 0 }
            ?.maxByOrNull { it.lastModified() }
        return ultima?.let { Uri.fromFile(it) }
    }

    // ----- Perfil y dispositivos -----

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