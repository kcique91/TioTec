package duoc.appmoviles.model

// Una publicación que llega desde la API de prueba (jsonplaceholder)
data class Post(
    val userId: Int,    // id del usuario que la creó
    val id: Int,        // id de la publicación
    val title: String, // título
    val body: String   // contenido
)