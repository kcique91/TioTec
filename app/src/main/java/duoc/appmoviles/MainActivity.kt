package duoc.appmoviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import duoc.appmoviles.navigation.NavigationEvent
import duoc.appmoviles.navigation.Screen
import duoc.appmoviles.screens.HomeScreen
import duoc.appmoviles.screens.PantallaBase
import duoc.appmoviles.ui.theme.TioTecTheme
import duoc.appmoviles.viewmodels.MainViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TioTecTheme {

                // ViewModel y NavController
                val viewModel: MainViewModel = viewModel()
                val navController = rememberNavController()

                // Escucha los eventos de navegación que emite el ViewModel
                LaunchedEffect(key1 = Unit) {
                    viewModel.navigationEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo -> {
                                navController.navigate(event.route.route) {
                                    event.popUpToRoute?.let {
                                        popUpTo(it.route) {
                                            inclusive = event.inclusive
                                        }
                                    }
                                    launchSingleTop = event.singleTop
                                    restoreState = true
                                }
                            }
                            is NavigationEvent.PopBackStack -> navController.popBackStack()
                            is NavigationEvent.NavigateUp -> navController.navigateUp()
                        }
                    }
                }

                // Estructura base con el NavHost
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(route = Screen.Home.route) {
                            HomeScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(route = Screen.Evaluation.route) {
                            PantallaBase(
                                titulo = "Evaluación",
                                descripcion = "Aquí irá la evaluación de seguridad digital.",
                                viewModel = viewModel
                            )
                        }
                        composable(route = Screen.Checklist.route) {
                            PantallaBase(
                                titulo = "Checklist",
                                descripcion = "Aquí irá el checklist de acciones preventivas.",
                                viewModel = viewModel
                            )
                        }
                        composable(route = Screen.History.route) {
                            PantallaBase(
                                titulo = "Historial",
                                descripcion = "Aquí irá el historial de evaluaciones y acciones.",
                                viewModel = viewModel
                            )
                        }
                        composable(route = Screen.Profile.route) {
                            PantallaBase(
                                titulo = "Perfil",
                                descripcion = "Aquí irá el perfil ficticio y los dispositivos de prueba.",
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}