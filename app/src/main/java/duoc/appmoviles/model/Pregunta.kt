package duoc.appmoviles.model

// Categorías de riesgo que revisa la evaluación
enum class Categoria(val titulo: String) {
    RESPALDOS("Respaldos"),
    ACTUALIZACIONES("Actualizaciones"),
    AUTENTICACION("Autenticación"),
    RECUPERACION("Recuperación de cuentas")
}

// Qué tan importante es corregir una brecha: Alta se atiende primero.
// "orden" sirve para ordenar la lista (1 va antes que 3)
enum class Prioridad(val titulo: String, val orden: Int) {
    ALTA("Alta", 1),
    MEDIA("Media", 2),
    BAJA("Baja", 3)
}

// Una pregunta de la evaluación, con su recomendación si la respuesta es "No"
data class Pregunta(
    val id: Int,
    val categoria: Categoria,
    val texto: String,
    val recomendacion: String,
    val prioridad: Prioridad = Prioridad.MEDIA
)