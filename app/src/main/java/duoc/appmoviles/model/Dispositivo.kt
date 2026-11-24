package duoc.appmoviles.model

// Un dispositivo de prueba tal como lo envía y recibe el microservicio ms-dispositivos.
// id es null al crearlo: lo pone el servidor.
data class Dispositivo(
    val id: Long? = null,
    val nombre: String = "",   // por ejemplo "Celular de prueba"
    val sistema: String = ""   // por ejemplo "Android 14"
)
