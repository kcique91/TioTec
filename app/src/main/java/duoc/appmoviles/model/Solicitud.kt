package duoc.appmoviles.model

// Una solicitud de asistencia tal como la envía y recibe el microservicio ms-solicitudes.
// id y fecha son null al crearla: los pone el servidor.
data class Solicitud(
    val id: Long? = null,
    val categoria: String = "",
    val descripcion: String = "",
    val estado: String = "Pendiente",   // Pendiente, En revisión o Resuelta
    val fecha: Long? = null             // milisegundos
)
