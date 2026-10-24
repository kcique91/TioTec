package duoc.appmoviles.model

// Una acción preventiva del checklist
data class Accion(
    val id: Int,                    // mismo id de la pregunta que la originó
    val categoria: Categoria,
    val texto: String,              // la recomendación a realizar
    val completada: Boolean = false, // false = pendiente, true = completada
    val prioridad: Prioridad = Prioridad.MEDIA // qué tan urgente es hacerla
)