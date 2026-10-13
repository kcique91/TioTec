package duoc.appmoviles.model

// Categorías de riesgo que revisa la evaluación
enum class Categoria(val titulo: String) {
    RESPALDOS("Respaldos"),
    ACTUALIZACIONES("Actualizaciones"),
    AUTENTICACION("Autenticación"),
    RECUPERACION("Recuperación de cuentas")
}

// Una pregunta de la evaluación, con su recomendación si la respuesta es "No"
data class Pregunta(
    val id: Int,
    val categoria: Categoria,
    val texto: String,
    val recomendacion: String
)