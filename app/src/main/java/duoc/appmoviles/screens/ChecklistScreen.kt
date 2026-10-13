package duoc.appmoviles.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import duoc.appmoviles.navigation.Screen
import duoc.appmoviles.viewmodels.ChecklistViewModel
import duoc.appmoviles.viewmodels.EvaluacionViewModel
import duoc.appmoviles.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    viewModel: MainViewModel,
    evaluacionViewModel: EvaluacionViewModel,
    checklistViewModel: ChecklistViewModel
) {
    val estadoEvaluacion by evaluacionViewModel.estado.collectAsState()
    val acciones by checklistViewModel.acciones.collectAsState()

    // Solo cuando la evaluación está terminada se actualizan las acciones.
    // Si no, se muestran las que ya estaban guardadas en la base de datos.
    LaunchedEffect(estadoEvaluacion.terminada, estadoEvaluacion.respuestas) {
        if (estadoEvaluacion.terminada) {
            checklistViewModel.cargarDesdeBrechas(evaluacionViewModel.brechas())
        }
    }

    val completadas = acciones.count { it.completada }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checklist") },
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

        if (acciones.isEmpty()) {
            // ----- Sin acciones: aún no hay evaluación o no hay brechas -----
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No tienes acciones pendientes por ahora.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Responde la evaluación para generar tu checklist.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.navigateTo(Screen.Evaluation) }) {
                    Text("Ir a la evaluación")
                }
            }

        } else {
            // ----- Con acciones: lista con casillas -----
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Completadas: $completadas de ${acciones.size}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { completadas.toFloat() / acciones.size },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(acciones) { accion ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = accion.completada,
                                    onCheckedChange = { checklistViewModel.alternar(accion.id) }
                                )
                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Text(
                                        text = accion.categoria.titulo,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = accion.texto,
                                        textDecoration = if (accion.completada)
                                            TextDecoration.LineThrough else TextDecoration.None
                                    )
                                    Text(
                                        text = if (accion.completada) "Completada" else "Pendiente",
                                        style = MaterialTheme.typography.labelMedium
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