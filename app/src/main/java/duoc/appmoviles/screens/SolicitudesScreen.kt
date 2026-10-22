package duoc.appmoviles.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import duoc.appmoviles.viewmodels.MainViewModel
import duoc.appmoviles.viewmodels.SolicitudesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesScreen(
    viewModel: MainViewModel,
    solicitudesViewModel: SolicitudesViewModel = viewModel()
) {
    // Solicitudes guardadas en la base de datos (se actualizan solas)
    val solicitudes by solicitudesViewModel.solicitudes.collectAsState()

    // Estado del formulario (categoría, descripción y error) vive en el ViewModel
    val form by solicitudesViewModel.form.collectAsState()

    val categorias = listOf("Respaldos", "Actualizaciones", "Autenticación", "Recuperación")

    // Formato para mostrar la fecha, por ejemplo 14-10-2026 18:30
    val formatoFecha = remember { SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitudes de asistencia") },
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

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {

            // ----- Formulario para pedir ayuda (ficticia) -----
            item {
                Text(
                    text = "Nueva solicitud",
                    style = MaterialTheme.typography.titleLarge
                )
                Text("Es una simulación: no se envía a ninguna persona real.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Categoría")
                // Fila con scroll horizontal por si las categorías no caben
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categorias.forEach { opcion ->
                        FilterChip(
                            selected = form.categoria == opcion,
                            onClick = { solicitudesViewModel.onCategoriaChange(opcion) },
                            label = { Text(opcion) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = form.descripcion,
                    onValueChange = solicitudesViewModel::onDescripcionChange,
                    label = { Text("Describe tu problema") },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    trailingIcon = {
                        if (form.errorDescripcion != null) {
                            Icon(Icons.Default.Warning, contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error)
                        }
                    },
                    isError = form.errorDescripcion != null,
                    supportingText = {
                        form.errorDescripcion?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { solicitudesViewModel.crearSolicitud() },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Enviar solicitud") }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
            }

            // ----- Lista de solicitudes -----
            if (solicitudes.isEmpty()) {
                item { Text("Aún no has creado solicitudes.") }
            } else {
                items(solicitudes) { solicitud ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = solicitud.categoria,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatoFecha.format(Date(solicitud.fecha)),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(solicitud.descripcion)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Estado: ${solicitud.estado}",
                                fontWeight = FontWeight.Bold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Simula que la solicitud avanza; se desactiva al llegar a "Resuelta"
                                Button(
                                    onClick = { solicitudesViewModel.avanzarEstado(solicitud) },
                                    enabled = solicitud.estado != "Resuelta"
                                ) { Text("Avanzar estado") }
                                TextButton(
                                    onClick = { solicitudesViewModel.eliminarSolicitud(solicitud.id) }
                                ) { Text("Eliminar") }
                            }
                        }
                    }
                }
            }
        }
    }
}