package duoc.appmoviles.model

// Un consejo de seguridad que llega desde la API
data class Consejo(
    val id: Int,
    val categoria: String,
    val titulo: String,
    val texto: String
)