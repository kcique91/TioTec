package duoc.appmoviles.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import duoc.appmoviles.viewmodels.MainViewModel
import duoc.appmoviles.viewmodels.VersionesViewModel

// Pantalla que muestra si cada versión de Android sigue recibiendo parches de seguridad.
// Los datos vienen de la API externa endoflife.date (se relaciona con la pregunta
// "¿Mantienes tu sistema actualizado?" de la evaluación).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionesScreen(
    viewModel: MainViewModel,
    versionesViewModel: VersionesViewModel = viewModel()
) {
    val versiones by versionesViewModel.versiones.collectAsState()
    val cargando by versionesViewModel.cargando.collectAsState()
    val error by versionesViewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Soporte de Android") },
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                // 1) Esperando la respuesta de la API
                cargando -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                // 2) La API falló: mensaje y botón para reintentar
                error != null -> Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { versionesViewModel.cargarVersiones() }) {
                        Text("Reintentar")
                    }
                }

                // 3) Lista de versiones
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        Text(
                            text = "¿Tu teléfono sigue recibiendo parches de seguridad?",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Revisa en Ajustes > Acerca del teléfono qué versión de Android " +
                                "tienes. Si ya no tiene soporte, tus datos quedan más expuestos."
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Versiones con soporte: ${versionesViewModel.cantidadConSoporte()} de ${versiones.size}",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Fuente: endoflife.date",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    items(versiones) { version ->
                        val conSoporte = versionesViewModel.tieneSoporte(version)
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Verde si tiene soporte, rojo si no
                                Icon(
                                    imageVector = if (conSoporte) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (conSoporte) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error
                                )
                                Column(modifier = Modifier.padding(start = 12.dp)) {
                                    Text(
                                        text = "Android ${version.cycle ?: "?"}",
                                        fontWeight = FontWeight.Bold
                                    )
                                    version.codename?.let { Text(it) }
                                    version.releaseDate?.let {
                                        Text(
                                            text = "Lanzamiento: $it",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                    Text(
                                        text = versionesViewModel.descripcionSoporte(version),
                                        color = if (conSoporte) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
