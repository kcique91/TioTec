package duoc.appmoviles.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import duoc.appmoviles.navigation.Screen
import duoc.appmoviles.viewmodels.MainViewModel

// Una opción de la barra inferior: a qué pantalla lleva, su texto y su ícono
private data class OpcionBarra(
    val pantalla: Screen,
    val texto: String,
    val icono: ImageVector
)

@Composable
fun BarraInferior(
    rutaActual: String?,
    viewModel: MainViewModel
) {
    // Las 4 pantallas principales de la app
    val opciones = listOf(
        OpcionBarra(Screen.Home, "Inicio", Icons.Default.Home),
        OpcionBarra(Screen.Checklist, "Checklist", Icons.Default.CheckCircle),
        OpcionBarra(Screen.History, "Historial", Icons.Default.DateRange),
        OpcionBarra(Screen.Profile, "Perfil", Icons.Default.Person)
    )

    NavigationBar {
        opciones.forEach { opcion ->
            // La opción está marcada si su ruta es la pantalla que se está viendo
            val seleccionada = rutaActual == opcion.pantalla.route

            NavigationBarItem(
                selected = seleccionada,
                onClick = {
                    // Si ya estás en esa pantalla, no hace nada
                    if (!seleccionada) {
                        viewModel.navigateTo(opcion.pantalla)
                    }
                },
                icon = { Icon(opcion.icono, contentDescription = opcion.texto) },
                label = { Text(opcion.texto) }
            )
        }
    }
}