package duoc.appmoviles.model

// Lista fija de preguntas de la evaluación (datos de prueba)
object BancoPreguntas {

    val preguntas = listOf(
        Pregunta(
            id = 1,
            categoria = Categoria.RESPALDOS,
            texto = "¿Haces copias de seguridad de tus fotos y documentos importantes?",
            recomendacion = "Activa una copia de seguridad automática en la nube o en un disco externo.",
            prioridad = Prioridad.MEDIA
        ),
        Pregunta(
            id = 2,
            categoria = Categoria.RESPALDOS,
            texto = "¿Revisaste en el último mes que tus respaldos se pueden abrir?",
            recomendacion = "Prueba restaurar un archivo de tu respaldo una vez al mes.",
            prioridad = Prioridad.BAJA
        ),
        Pregunta(
            id = 3,
            categoria = Categoria.ACTUALIZACIONES,
            texto = "¿Tu celular tiene instaladas las últimas actualizaciones del sistema?",
            recomendacion = "Ve a Ajustes, busca actualizaciones del sistema e instálalas.",
            prioridad = Prioridad.ALTA
        ),
        Pregunta(
            id = 4,
            categoria = Categoria.ACTUALIZACIONES,
            texto = "¿Tus aplicaciones se actualizan de forma automática?",
            recomendacion = "Activa las actualizaciones automáticas en la tienda de aplicaciones.",
            prioridad = Prioridad.MEDIA
        ),
        Pregunta(
            id = 5,
            categoria = Categoria.AUTENTICACION,
            texto = "¿Usas verificación en dos pasos en tu correo y redes sociales?",
            recomendacion = "Activa la verificación en dos pasos en la configuración de seguridad de cada cuenta.",
            prioridad = Prioridad.ALTA
        ),
        Pregunta(
            id = 6,
            categoria = Categoria.AUTENTICACION,
            texto = "¿Usas contraseñas distintas para cada cuenta importante?",
            recomendacion = "Crea una contraseña única por cuenta y guárdalas en un gestor de contraseñas.",
            prioridad = Prioridad.ALTA
        ),
        Pregunta(
            id = 7,
            categoria = Categoria.RECUPERACION,
            texto = "¿Tus cuentas tienen un correo de recuperación actualizado?",
            recomendacion = "Revisa y actualiza el correo de recuperación en la configuración de cada cuenta.",
            prioridad = Prioridad.MEDIA
        ),
        Pregunta(
            id = 8,
            categoria = Categoria.RECUPERACION,
            texto = "¿Tienes un número de teléfono de recuperación asociado a tus cuentas?",
            recomendacion = "Agrega un teléfono de recuperación en la sección de seguridad de tus cuentas.",
            prioridad = Prioridad.BAJA
        )
    )
}