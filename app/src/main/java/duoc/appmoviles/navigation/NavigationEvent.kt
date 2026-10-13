package duoc.appmoviles.navigation

// Representa los distintos tipos de eventos de navegación
sealed class NavigationEvent {

    // Navegar a una pantalla específica
    // popUpToRoute: pantalla hasta la cual se limpia la pila (opcional)
    // inclusive: si es true, también se elimina esa pantalla de la pila
    // singleTop: si es true, evita copias repetidas de la misma pantalla
    data class NavigateTo(
        val route: Screen,
        val popUpToRoute: Screen? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    // Volver a la pantalla anterior
    object PopBackStack : NavigationEvent()

    // Navegar "hacia arriba" en la jerarquía de la app
    object NavigateUp : NavigationEvent()
}

