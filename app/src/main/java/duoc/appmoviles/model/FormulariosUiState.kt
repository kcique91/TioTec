package duoc.appmoviles.model

// Estado del formulario del perfil ficticio (null en un error = sin error)
data class PerfilFormState(
    val nombre: String = "",
    val nivel: String = "Básico",
    val errorNombre: String? = null
)

// Estado del formulario para registrar un dispositivo de prueba
data class DispositivoFormState(
    val nombre: String = "",
    val sistema: String = "",
    val errorNombre: String? = null,
    val errorSistema: String? = null
)

// Estado del formulario de solicitudes de asistencia
data class SolicitudFormState(
    val categoria: String = "Respaldos",
    val descripcion: String = "",
    val errorDescripcion: String? = null
)
