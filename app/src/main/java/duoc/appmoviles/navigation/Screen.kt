package duoc.appmoviles.navigation

// Clase sellada: solo existen las rutas que definimos aquí dentro
sealed class Screen(val route: String) {

    // Pantalla de inicio
    data object Home : Screen("home_page")

    // Evaluación de seguridad digital
    data object Evaluation : Screen("evaluation_page")

    // Checklist de acciones preventivas
    data object Checklist : Screen("checklist_page")

    // Historial de evaluaciones y acciones
    data object History : Screen("history_page")

    // Perfil ficticio y dispositivos de prueba
    data object Profile : Screen("profile_page")


    // Consejos de seguridad que llegan desde la API
    data object Consejos : Screen("consejos_page")
}

