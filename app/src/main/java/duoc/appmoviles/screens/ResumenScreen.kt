package duoc.appmoviles.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import duoc.appmoviles.viewmodels.MainViewModel
import duoc.appmoviles.viewmodels.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(
    viewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    // Es el mismo ViewModel del formulario, así que los datos ya están aquí
    val estado by usuarioViewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen del registro") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Resumen del Registro",
                style = MaterialTheme.typography.headlineMedium
            )
            Text("Nombre: ${estado.nombre}")
            Text("Correo: ${estado.correo}")
            Text("Dirección: ${estado.direccion}")
            // La contraseña no se muestra: solo un asterisco por cada letra
            Text("Contraseña: ${"*".repeat(estado.clave.length)}")
            Text("Términos: ${if (estado.aceptaTerminos) "Aceptados" else "No aceptados"}")
        }
    }
}