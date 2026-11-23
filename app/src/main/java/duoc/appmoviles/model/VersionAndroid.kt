package duoc.appmoviles.model

// Una versión de Android según la API pública endoflife.date.
// Ejemplo: {"cycle":"14","codename":"Upside Down Cake","releaseDate":"2023-10-04","eol":false}
data class VersionAndroid(
    val cycle: String? = null,        // número de versión, por ejemplo "14"
    val codename: String? = null,     // nombre clave
    val releaseDate: String? = null,  // fecha de lanzamiento (aaaa-mm-dd)
    // Fin del soporte: puede venir como false (sigue con soporte), true (ya no tiene)
    // o como una fecha "aaaa-mm-dd". Por eso es de tipo Any?
    val eol: Any? = null
) {

    // true si a la fecha "hoy" (aaaa-mm-dd) la versión sigue recibiendo parches de seguridad.
    // Las fechas aaaa-mm-dd se pueden comparar como texto.
    fun tieneSoporte(hoy: String): Boolean = when (eol) {
        is Boolean -> !eol
        is String -> eol > hoy
        else -> false
    }

    // Texto que se muestra en la tarjeta
    fun descripcionSoporte(hoy: String): String = when (eol) {
        false -> "Con soporte de seguridad"
        true -> "Sin soporte de seguridad"
        is String -> if (eol > hoy) "Con soporte hasta $eol" else "Sin soporte desde $eol"
        else -> "Sin información de soporte"
    }
}
