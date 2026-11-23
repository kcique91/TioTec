package duoc.appmoviles.viewmodels

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import duoc.appmoviles.data.AppDatabase
import duoc.appmoviles.data.PerfilEntity
import duoc.appmoviles.model.PerfilFormState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    // DAO del perfil (los dispositivos ahora viven en el microservicio ms-dispositivos)
    private val perfilDao = AppDatabase.obtener(application).perfilDao()

    // Perfil guardado (null si todavía no se ha creado)
    val perfil: StateFlow<PerfilEntity?> = perfilDao.obtener()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

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

    // ----- Formulario del perfil (estado y validación) -----

    // Estado del formulario del perfil: privado para modificar, público solo lectura
    private val _formPerfil = MutableStateFlow(PerfilFormState())
    val formPerfil: StateFlow<PerfilFormState> = _formPerfil.asStateFlow()

    init {
        // Si ya hay un perfil guardado, se rellena el formulario con sus datos
        viewModelScope.launch {
            perfilDao.obtener().first()?.let { guardado ->
                _formPerfil.update { it.copy(nombre = guardado.nombre, nivel = guardado.nivel) }
            }
        }
    }

    // Actualiza el nombre del perfil y limpia su error
    fun onNombrePerfilChange(valor: String) {
        _formPerfil.update { it.copy(nombre = valor, errorNombre = null) }
    }

    // Actualiza el nivel de experiencia elegido
    fun onNivelChange(valor: String) {
        _formPerfil.update { it.copy(nivel = valor) }
    }

    // Valida el perfil; si está correcto lo guarda en Room. Devuelve true si se guardó
    fun guardarPerfil(): Boolean {
        val form = _formPerfil.value
        val error = when {
            form.nombre.isBlank() -> "Campo obligatorio"
            form.nombre.trim().length < 3 -> "Debe tener al menos 3 caracteres"
            else -> null
        }
        _formPerfil.update { it.copy(errorNombre = error) }
        if (error != null) return false

        viewModelScope.launch {
            perfilDao.guardar(PerfilEntity(nombre = form.nombre.trim(), nivel = form.nivel))
        }
        return true
    }
}
