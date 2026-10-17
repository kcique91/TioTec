package duoc.appmoviles.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import duoc.appmoviles.navigation.Screen
import duoc.appmoviles.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    // Estado del menú lateral (abierto o cerrado)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Permite abrir y cerrar el menú dentro de una corutina
    val scope = rememberCoroutineScope()

    // Opciones del menú: texto y pantalla a la que lleva cada una
    val opciones = listOf(
        "Evaluación" to Screen.Evaluation,
        "Checklist" to Screen.Checklist,
        "Historial" to Screen.History,
        "Perfil" to Screen.Profile,
        "Consejos" to Screen.Consejos,
        "Solicitudes" to Screen.Solicitudes,
        "Registro" to Screen.Registro

    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú TíoTec", modifier = Modifier.padding(16.dp))
                // Crea un ítem del menú por cada opción de la lista
                opciones.forEach { (texto, pantalla) ->
                    NavigationDrawerItem(
                        label = { Text(texto) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            viewModel.navigateTo(pantalla)
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("TíoTec Preventivo") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Revisa tus hábitos de seguridad digital",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Evalúa respaldos, actualizaciones y autenticación, y sigue tus acciones pendientes.",
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { viewModel.navigateTo(Screen.Evaluation) }) {
                    Text("Iniciar evaluación")
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { viewModel.navigateTo(Screen.Checklist) }) {
                    Text("Ver mi checklist")
                }
            }
        }
    }
}
